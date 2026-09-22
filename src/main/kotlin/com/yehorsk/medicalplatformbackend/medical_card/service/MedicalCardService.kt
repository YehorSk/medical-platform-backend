package com.yehorsk.medicalplatformbackend.medical_card.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.audit_logs.database.repository.AuditLogRepository
import com.yehorsk.medicalplatformbackend.common.domain.events.audit.AuditLogEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.exceptions.types.AccessDeniedException
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.medical_card.exceptions.types.InvalidMedicalCardDataException
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.request.UpdateMedicalCardRequestDto
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardResponseDto
import com.yehorsk.medicalplatformbackend.medical_card.service.mappers.toMedicalCardResponseDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MedicalCardService(
    private val medicalCardRepository: MedicalCardRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val auditLogRepository: AuditLogRepository,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getMedicalCardByPatientId(patientId: UserId): MedicalCardResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()
        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(patientId)
            ?: throw PatientNotFoundException()

        val doctorId = user.id ?: throw AccessDeniedException()
        val inDoctorsSet = medicalCard.doctors.any { phd ->
            phd.doctor.id == doctorId && phd.status == AccessStatus.APPROVED
        }
        if (!inDoctorsSet) throw AccessDeniedException()

        auditLogRepository.save(
            AuditLogEntity(
                accessedById = doctorId,
                accessedBy = user,
                medicalCardId = medicalCard.id ?: throw PatientNotFoundException(),
                medicalCard = medicalCard,
                action = AuditLogAction.READ,
                detail = "Doctor accessed patient medical card"
            )
        )

        val patient = medicalCard.patient ?: throw PatientNotFoundException()
        val doctorUsername = "${user.firstName} ${user.lastName}".trim()
        val patientUsername = "${patient.firstName} ${patient.lastName}".trim()

        eventPublisher.publish(
            AuditLogEvent.MedicalCardAccessed(
                patientId = patient.id ?: throw PatientNotFoundException(),
                patientEmail = patient.email,
                doctorId = doctorId,
                doctorUsername = doctorUsername,
                patientUsername = patientUsername
            )
        )

        return medicalCard.toMedicalCardResponseDto()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getMyMedicalCard(): MedicalCardResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()
        val userId = user.id ?: throw PatientNotFoundException()

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(userId)
            ?: throw PatientNotFoundException()

        return medicalCard.toMedicalCardResponseDto()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun updateMyMedicalCard(request: UpdateMedicalCardRequestDto): MedicalCardResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()
        val userId = user.id ?: throw PatientNotFoundException()

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(userId)
            ?: throw PatientNotFoundException()

        request.dateOfBirth?.let {
            medicalCard.dateOfBirth = try {
                LocalDate.parse(it)
            } catch (_: Exception) {
                throw InvalidMedicalCardDataException("Invalid dateOfBirth format. Expected yyyy-MM-dd")
            }
        }

        request.bloodType?.let { medicalCard.bloodType = it }
        request.gender?.let { medicalCard.gender = it }
        request.insuranceCompany?.let { medicalCard.insuranceCompany = it }

        if (request.insuranceNumber != null) medicalCard.insuranceNumber = request.insuranceNumber

        val saved = medicalCardRepository.save(medicalCard)
        return saved.toMedicalCardResponseDto()
    }
}
