package com.yehorsk.medicalplatformbackend.medical_records.service

import com.yehorsk.medicalplatformbackend.common.domain.events.attachment.AttachmentEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.infra.antivirus.ClamAvClient
import com.yehorsk.medicalplatformbackend.common.infra.antivirus.ScanResult
import com.yehorsk.medicalplatformbackend.common.service.FileStorageService
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.AttachmentStatus
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordAttachmentEntity
import com.yehorsk.medicalplatformbackend.medical_records.database.repository.MedicalRecordAttachmentRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class AttachmentScanService(
    private val attachmentRepository: MedicalRecordAttachmentRepository,
    private val fileStorageService: FileStorageService,
    private val clamAvClient: ClamAvClient,
    private val eventPublisher: EventPublisher,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Scans one quarantined file and moves it to its final status.
     * Safe to call more than once for the same id: anything not in PENDING_SCAN is skipped.
     * No DB transaction is held while the file is streamed to ClamAV.
     */
    fun scan(attachmentId: MedicalRecordAttachmentId) {
        val attachment = attachmentRepository.findById(attachmentId).orElse(null) ?: run {
            logger.warn("Attachment {} not found, skipping scan", attachmentId)
            return
        }
        if (attachment.status != AttachmentStatus.PENDING_SCAN) {
            logger.debug("Attachment {} already {}, skipping scan", attachmentId, attachment.status)
            return
        }

        var lastError: Exception? = null
        for (attempt in 1..MAX_ATTEMPTS) {
            attachment.scanAttempts += 1
            try {
                val result = fileStorageService.openQuarantine(attachmentId).use { clamAvClient.scan(it) }
                when (result) {
                    is ScanResult.Clean -> {
                        fileStorageService.promote(attachmentId)
                        attachment.status = AttachmentStatus.AVAILABLE
                        logger.info("Attachment {} is clean, promoted to main bucket", attachmentId)
                    }
                    is ScanResult.Infected -> {
                        fileStorageService.deleteFromQuarantine(attachmentId)
                        attachment.status = AttachmentStatus.INFECTED
                        attachment.virusName = result.signature
                        logger.warn("Attachment {} is infected ({}), deleted", attachmentId, result.signature)
                    }
                }
                attachment.scannedAt = Instant.now()
                attachmentRepository.save(attachment)
                if (attachment.status == AttachmentStatus.INFECTED) notifyScanFailed(attachment)
                return
            } catch (e: Exception) {
                lastError = e
                logger.warn("Scan attempt {}/{} for attachment {} failed: {}", attempt, MAX_ATTEMPTS, attachmentId, e.message)
                if (attempt < MAX_ATTEMPTS) Thread.sleep(BACKOFF_MS * (1L shl (attempt - 1)))
            }
        }

        attachment.status = AttachmentStatus.SCAN_FAILED
        attachment.scannedAt = Instant.now()
        attachmentRepository.save(attachment)
        notifyScanFailed(attachment)
        logger.error("Attachment {} scan failed after {} attempts, left in quarantine", attachmentId, MAX_ATTEMPTS, lastError)
    }

    // Lazy proxies: reading their ids doesn't need a DB session
    private fun notifyScanFailed(attachment: MedicalRecordAttachmentEntity) {
        eventPublisher.publish(
            AttachmentEvent.ScanFailed(
                attachmentId = attachment.id!!,
                medicalRecordId = attachment.medicalRecord.id!!,
                doctorId = attachment.uploadedBy.id!!,
                status = attachment.status.name,
            )
        )
    }

    /**
     * Safety net for lost messages (broker down at publish time, app killed mid-scan):
     * re-queues files that have been waiting for a scan for too long.
     */
    @Scheduled(fixedDelay = SWEEP_INTERVAL_MS, initialDelay = SWEEP_INTERVAL_MS)
    fun requeueStalePendingScans() {
        val threshold = Instant.now().minus(STALE_AFTER)
        val stale = attachmentRepository.findAllByStatusAndCreatedAtBefore(AttachmentStatus.PENDING_SCAN, threshold)
        if (stale.isEmpty()) return

        logger.info("Re-queuing {} attachment(s) stuck in PENDING_SCAN", stale.size)
        stale.forEach { eventPublisher.publish(AttachmentEvent.Uploaded(attachmentId = it.id!!)) }
    }

    companion object {
        private const val MAX_ATTEMPTS = 3
        private const val BACKOFF_MS = 2_000L
        private const val SWEEP_INTERVAL_MS = 5 * 60 * 1000L
        private val STALE_AFTER: Duration = Duration.ofMinutes(10)
    }
}
