package com.yehorsk.medicalplatformbackend.medical_records.service.dto

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.appointments.exceptions.AppointmentNotFoundException
import com.yehorsk.medicalplatformbackend.appointments.service.AppointmentService
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordEntity
import com.yehorsk.medicalplatformbackend.medical_records.database.repository.MedicalRecordRepository
import com.yehorsk.medicalplatformbackend.medical_records.mappers.toBodyPartSelection
import com.yehorsk.medicalplatformbackend.medical_records.service.MedicalRecordAttachmentService
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.request.CreateMedicalRecordRequestDto
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.MedicalRecordCreatedResponseDto
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.MedicalRecordDetailsResponseDto
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordAlreadyExistsException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordNotFoundException
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class MedicalRecordService(
    private val medicalCardRepository: MedicalCardRepository,
    private val medicalRecordRepository: MedicalRecordRepository,
    private val appointmentRepository: AppointmentRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val attachmentService: MedicalRecordAttachmentService,
) {

    private val logger = LoggerFactory.getLogger(MedicalRecordService::class.java)

    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    fun createMedicalRecord(
        request: CreateMedicalRecordRequestDto,
        files: List<MultipartFile> = emptyList(),
    ): MedicalRecordCreatedResponseDto {
        logger.info("Creating medical record: appointmentId=${request.appointmentId}, patientId=${request.patientId}")

        // Text fields are validated by @Valid; check every file's type before anything is saved
        val validatedFiles = attachmentService.validateFiles(files)

        val currentUser = currentUserProvider.getCurrentUserEntity()

        val appointment = appointmentRepository.findByDoctorIdAndId(currentUser.id!!, request.appointmentId)
            ?: run {
                logger.warn (
                    "Appointment not found. doctorId=${currentUser.id}, " +
                            "appointmentId=${request.appointmentId}"
                    )
                throw AppointmentNotFoundException()
            }

        // One record per appointment (unique appointment_id). Checked before anything is saved or uploaded
        if (medicalRecordRepository.existsByAppointmentId(request.appointmentId)) {
            logger.warn("Medical record already exists. doctorId=${currentUser.id}, appointmentId=${request.appointmentId}")
            throw MedicalRecordAlreadyExistsException()
        }

        if (appointment.patient.id != request.patientId) {
            logger.warn (
                "Appointment patient mismatch. doctorId=${currentUser.id}, " +
                        "appointmentId=${request.appointmentId}, " +
                        "expectedPatientId=${request.patientId}, " +
                        "actualPatientId=${appointment.patient.id}"
            )
            throw AppointmentNotFoundException()
        }

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(request.patientId)
            ?: run {
                logger.warn(
                    "Medical card not found. patientId=${request.patientId}, " +
                            "doctorId=${currentUser.id}"
                )
                throw PatientNotFoundException()
            }

        val medicalRecord = MedicalRecordEntity(
            appointment = appointment,
            title = request.title,
            doctor = currentUser,
            medicalCard = medicalCard,
            type = request.type,
            diagnosis = request.diagnosis,
            recommendations = request.recommendations,
            bodyParts = request.bodyParts.map { it.toBodyPartSelection() }.toMutableSet()
        )

        appointment.status = AppointmentStatus.COMPLETED
        appointmentRepository.save(appointment)

        val saved = medicalRecordRepository.save(medicalRecord)

        // Files are only copied to quarantine here; the antivirus scan runs later from the queue.
        // A file that fails to store is returned as UPLOAD_FAILED, the record itself is still created.
        val attachments = attachmentService.storeAttachments(saved, currentUser, validatedFiles)

        return MedicalRecordCreatedResponseDto(
            id = saved.id!!,
            attachments = attachments,
            message = "Medical record created successfully"
        )
    }

    @Transactional
    fun getMedicalRecord(recordId: MedicalRecordId): MedicalRecordDetailsResponseDto {
        val currentUser = currentUserProvider.getCurrentUserEntity()
        val record = medicalRecordRepository.findById(recordId).orElseThrow { MedicalRecordNotFoundException() }
        attachmentService.checkCanView(currentUser, record)

        return MedicalRecordDetailsResponseDto(
            id = record.id!!,
            title = record.title,
            type = record.type,
            diagnosis = record.diagnosis,
            description = record.description,
            recommendations = record.recommendations,
            bodyParts = record.bodyParts.toList(),
            doctorId = record.doctor.id!!,
            doctorName = listOfNotNull(record.doctor.title, record.doctor.firstName, record.doctor.lastName)
                .joinToString(" "),
            patientId = record.medicalCard.patient?.id,
            appointmentId = record.appointment?.id,
            createdAt = record.createdAt,
            updatedAt = record.updatedAt,
            attachments = attachmentService.getAttachmentDtos(recordId),
        )
    }

}
