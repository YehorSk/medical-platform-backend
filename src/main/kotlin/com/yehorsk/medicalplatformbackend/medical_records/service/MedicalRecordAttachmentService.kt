package com.yehorsk.medicalplatformbackend.medical_records.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.audit_logs.database.repository.AuditLogRepository
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.events.attachment.AttachmentEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.service.FileStorageService
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.AttachmentStatus
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordAttachmentEntity
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordEntity
import com.yehorsk.medicalplatformbackend.medical_records.database.repository.MedicalRecordAttachmentRepository
import com.yehorsk.medicalplatformbackend.medical_records.database.repository.MedicalRecordRepository
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.AttachmentNotAvailableException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.AttachmentNotFoundException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.InvalidAttachmentException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordAccessDeniedException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordNotFoundException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.UnsupportedAttachmentTypeException
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.AttachmentDownload
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.AttachmentResponseDto
import org.apache.tika.Tika
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest

@ConfigurationProperties("medical-platform.attachments")
data class AttachmentProperties(
    val maxFilesPerRecord: Int = 10,
    // Checked against the type detected from the file's bytes, never the client's Content-Type header
    val allowedContentTypes: Set<String> = setOf(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "image/webp",
        "application/dicom",
        "text/plain",
    ),
)

// A file that passed type validation, with the type detected from its contents
class ValidatedFile(val file: MultipartFile, val contentType: String)

@Service
class MedicalRecordAttachmentService(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val attachmentRepository: MedicalRecordAttachmentRepository,
    private val auditLogRepository: AuditLogRepository,
    private val fileStorageService: FileStorageService,
    private val currentUserProvider: CurrentUserProvider,
    private val eventPublisher: EventPublisher,
    private val props: AttachmentProperties,
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val tika = Tika()

    /**
     * Rejects the whole request if any file is empty, of a disallowed type, or over the per-record limit.
     * Called before the record is created, so a bad file never produces a half-saved record.
     */
    fun validateFiles(files: List<MultipartFile>, alreadyAttached: Int = 0): List<ValidatedFile> {
        if (alreadyAttached + files.size > props.maxFilesPerRecord) {
            throw InvalidAttachmentException("A medical record can have at most ${props.maxFilesPerRecord} attachments")
        }
        return files.map { file ->
            if (file.isEmpty) throw InvalidAttachmentException("File '${file.originalFilename}' is empty")
            val detected = file.inputStream.use { tika.detect(it, file.originalFilename) }
            if (detected !in props.allowedContentTypes) {
                logger.warn("Rejected upload '{}': detected type {}", file.originalFilename, detected)
                throw UnsupportedAttachmentTypeException(detected)
            }
            ValidatedFile(file, detected)
        }
    }

    /**
     * Saves one row per file and copies the file into the quarantine bucket.
     * A file that cannot be stored is kept as UPLOAD_FAILED instead of failing the request,
     * so the record is still created and the doctor can re-upload that file later.
     * Scan events are published only after the transaction commits.
     */
    fun storeAttachments(
        record: MedicalRecordEntity,
        uploadedBy: UserEntity,
        files: List<ValidatedFile>
    ): List<AttachmentResponseDto> {
        return files.map { validated ->
            val file = validated.file
            val attachment = attachmentRepository.save(
                MedicalRecordAttachmentEntity(
                    medicalRecord = record,
                    uploadedBy = uploadedBy,
                    originalFilename = sanitizeFilename(file.originalFilename),
                    contentType = validated.contentType,
                    sizeBytes = file.size,
                    sha256 = sha256(file),
                )
            )
            val attachmentId = attachment.id!!
            try {
                fileStorageService.uploadToQuarantine(attachmentId, file, validated.contentType)
                eventPublisher.publishAfterCommit(AttachmentEvent.Uploaded(attachmentId = attachmentId))
            } catch (e: Exception) {
                logger.error("Failed to store attachment {} ('{}') for record {}", attachmentId, file.originalFilename, record.id, e)
                attachment.status = AttachmentStatus.UPLOAD_FAILED
            }
            attachment.toDto()
        }
    }

    /**
     * Adds files to an existing record, e.g. to re-upload ones that failed.
     * Only the doctor who wrote the record may do this, and every change is written to the audit log.
     */
    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    fun addAttachments(recordId: MedicalRecordId, files: List<MultipartFile>): List<AttachmentResponseDto> {
        val currentUser = currentUserProvider.getCurrentUserEntity()
        val record = medicalRecordRepository.findById(recordId).orElseThrow { MedicalRecordNotFoundException() }
        if (record.doctor.id != currentUser.id) throw MedicalRecordAccessDeniedException()
        if (files.isEmpty()) throw InvalidAttachmentException("No files provided")

        val validated = validateFiles(files, alreadyAttached = countActiveAttachments(recordId))
        val stored = storeAttachments(record, currentUser, validated)

        auditLogRepository.save(
            AuditLogEntity(
                accessedById = currentUser.id!!,
                accessedBy = currentUser,
                medicalCardId = record.medicalCard.id!!,
                medicalCard = record.medicalCard,
                action = AuditLogAction.UPDATE,
                detail = "Added ${stored.size} attachment(s) to medical record $recordId: " +
                        stored.joinToString { "${it.originalFilename} (${it.id})" }
            )
        )

        logger.info("Doctor {} added {} attachment(s) to record {}", currentUser.id, stored.size, recordId)
        return stored
    }

    @Transactional(readOnly = true)
    fun getAttachments(recordId: MedicalRecordId): List<AttachmentResponseDto> {
        val record = medicalRecordRepository.findById(recordId).orElseThrow { MedicalRecordNotFoundException() }
        checkCanView(currentUserProvider.getCurrentUserEntity(), record)
        return getAttachmentDtos(recordId)
    }

    // No access check: callers must have checked access to the record already
    fun getAttachmentDtos(recordId: MedicalRecordId): List<AttachmentResponseDto> =
        attachmentRepository.findAllByMedicalRecordIdOrderByCreatedAtAsc(recordId).map { it.toDto() }

    @Transactional(readOnly = true)
    fun downloadAttachment(recordId: MedicalRecordId, attachmentId: MedicalRecordAttachmentId): AttachmentDownload {
        val record = medicalRecordRepository.findById(recordId).orElseThrow { MedicalRecordNotFoundException() }
        checkCanView(currentUserProvider.getCurrentUserEntity(), record)

        val attachment = attachmentRepository.findByIdAndMedicalRecordId(attachmentId, recordId)
            ?: throw AttachmentNotFoundException()

        // Only scanned, clean files are ever served, and only from the main bucket
        if (attachment.status != AttachmentStatus.AVAILABLE) {
            throw AttachmentNotAvailableException(attachment.status.name)
        }

        return AttachmentDownload(
            filename = attachment.originalFilename,
            contentType = attachment.contentType,
            sizeBytes = attachment.sizeBytes,
            content = fileStorageService.download(attachmentId),
        )
    }

    // Author doctor, the patient who owns the card, or a doctor with APPROVED access to the patient's card
    fun checkCanView(user: UserEntity, record: MedicalRecordEntity) {
        if (!record.canBeViewedBy(user.id!!)) throw MedicalRecordAccessDeniedException()
    }

    // Failed files don't count towards the limit, so the doctor can always re-upload them
    private fun countActiveAttachments(recordId: MedicalRecordId): Int =
        attachmentRepository.findAllByMedicalRecordIdOrderByCreatedAtAsc(recordId)
            .count { it.status == AttachmentStatus.PENDING_SCAN || it.status == AttachmentStatus.AVAILABLE }

    private fun sha256(file: MultipartFile): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream.use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    // Keep only the base name and strip characters that could break the Content-Disposition header
    private fun sanitizeFilename(name: String?): String {
        val base = name?.substringAfterLast('/')?.substringAfterLast('\\')?.trim().orEmpty()
        val cleaned = base.replace(Regex("[\\p{Cntrl}\"]"), "_").take(255)
        return cleaned.ifBlank { "file" }
    }

    private fun MedicalRecordAttachmentEntity.toDto() = AttachmentResponseDto(
        id = id!!,
        originalFilename = originalFilename,
        contentType = contentType,
        sizeBytes = sizeBytes,
        status = status,
        virusName = virusName,
        createdAt = createdAt,
        scannedAt = scannedAt,
    )
}
