package com.yehorsk.medicalplatformbackend.medical_card.service

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.common.exceptions.types.AccessDeniedException
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.request.UpdateMedicalCardRequestDto
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardResponseDto
import com.yehorsk.medicalplatformbackend.medical_card.service.mappers.toMedicalCardResponseDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.time.LocalDate
import com.yehorsk.medicalplatformbackend.medical_card.exceptions.types.InvalidMedicalCardDataException

@Service
class MedicalCardService(
    private val medicalCardRepository: MedicalCardRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun getMedicalCardByPatientId(patientId: UserId): MedicalCardResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(patientId)
            ?: throw PatientNotFoundException()

        when (user.role) {
            UserRole.PATIENT -> {
                if (user.id != patientId) throw AccessDeniedException()
            }
            UserRole.DOCTOR -> {
                val doctorId = user.id ?: throw AccessDeniedException()
                val inDoctorsSet = medicalCard.doctors.any { phd ->
                    phd.doctor.id == doctorId && phd.status == AccessStatus.APPROVED
                }
                if (!inDoctorsSet) throw AccessDeniedException()
            }
            else -> throw AccessDeniedException()
        }

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
        if (request.insuranceNumber != null) medicalCard.insuranceNumber = request.insuranceNumber

        val saved = medicalCardRepository.save(medicalCard)
        return saved.toMedicalCardResponseDto()
    }

}





