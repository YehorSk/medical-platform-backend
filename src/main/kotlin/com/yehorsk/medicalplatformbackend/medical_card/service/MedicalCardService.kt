package com.yehorsk.medicalplatformbackend.medical_card.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.audit_logs.service.AuditLogService
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
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
    private val auditLogService: AuditLogService,
    private val eventPublisher: EventPublisher
) {

    /**
     * Any signed-in user may call this; every attempt on an existing card is audited.
     * Only a doctor with APPROVED access gets the card, everyone else gets 403 and a DENIED entry.
     */
    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun getMedicalCardByPatientId(patientId: UserId): MedicalCardResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()
        val userId = user.id ?: throw AccessDeniedException()
        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(patientId)
            ?: throw PatientNotFoundException()

        if (user.role != UserRole.DOCTOR) {
            auditLogService.record(
                user, medicalCard, AuditLogAction.READ, AuditLogOutcome.DENIED,
                "User with role ${user.role} attempted to access patient medical card"
            )
            throw AccessDeniedException()
        }

        val access = medicalCard.doctors.firstOrNull { it.doctor.id == userId }
        if (access?.status != AccessStatus.APPROVED) {
            val reason = access?.let { "access status is ${it.status}" } ?: "no access request"
            auditLogService.record(
                user, medicalCard, AuditLogAction.READ, AuditLogOutcome.DENIED,
                "Doctor attempted to access patient medical card without approved access ($reason)"
            )
            throw AccessDeniedException()
        }

        auditLogService.record(
            user, medicalCard, AuditLogAction.READ, AuditLogOutcome.SUCCESS,
            "Doctor accessed patient medical card"
        )

        val patient = medicalCard.patient ?: throw PatientNotFoundException()
        val doctorUsername = "${user.firstName} ${user.lastName}".trim()
        val patientUsername = "${patient.firstName} ${patient.lastName}".trim()

        eventPublisher.publish(
            AuditLogEvent.MedicalCardAccessed(
                patientId = patient.id ?: throw PatientNotFoundException(),
                patientEmail = patient.email,
                doctorId = userId,
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

        auditLogService.record(
            user, medicalCard, AuditLogAction.READ, AuditLogOutcome.SUCCESS,
            "Patient viewed own medical card"
        )

        return medicalCard.toMedicalCardResponseDto()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun updateMyMedicalCard(request: UpdateMedicalCardRequestDto): MedicalCardResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()
        val userId = user.id ?: throw PatientNotFoundException()

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(userId)
            ?: throw PatientNotFoundException()

        // Field names only, values stay out of the log
        val changedFields = buildList {
            if (request.dateOfBirth != null) add("dateOfBirth")
            if (request.bloodType != null) add("bloodType")
            if (request.gender != null) add("gender")
            if (request.insuranceCompany != null) add("insuranceCompany")
            if (request.insuranceNumber != null) add("insuranceNumber")
        }

        request.dateOfBirth?.let {
            medicalCard.dateOfBirth = try {
                LocalDate.parse(it)
            } catch (_: Exception) {
                auditLogService.record(
                    user, medicalCard, AuditLogAction.UPDATE, AuditLogOutcome.FAILED,
                    "Patient failed to update own medical card: invalid dateOfBirth"
                )
                throw InvalidMedicalCardDataException("Invalid dateOfBirth format. Expected yyyy-MM-dd")
            }
        }

        request.bloodType?.let { medicalCard.bloodType = it }
        request.gender?.let { medicalCard.gender = it }
        request.insuranceCompany?.let { medicalCard.insuranceCompany = it }

        if (request.insuranceNumber != null) medicalCard.insuranceNumber = request.insuranceNumber

        val saved = medicalCardRepository.save(medicalCard)

        auditLogService.record(
            user, saved, AuditLogAction.UPDATE, AuditLogOutcome.SUCCESS,
            "Patient updated own medical card: " +
                    changedFields.ifEmpty { listOf("no fields") }.joinToString()
        )

        return saved.toMedicalCardResponseDto()
    }
}
