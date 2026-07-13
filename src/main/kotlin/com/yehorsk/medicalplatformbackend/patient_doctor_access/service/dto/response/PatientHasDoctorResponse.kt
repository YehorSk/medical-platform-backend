package com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardResponseDto
import java.time.Instant

data class PatientHasDoctorResponse(
    val id: PatientHasDoctorId,
    val medicalCard: MedicalCardResponseDto,
    val doctor: UserResponseDto,
    val status: AccessStatus,
    val createdAt: Instant
)
