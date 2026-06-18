package com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.user.service.dto.response.UserResponseDto
import java.time.Instant

data class PatientHasDoctorResponse(
    val id: PatientHasDoctorId,
    val patient: UserResponseDto,
    val doctor: UserResponseDto,
    val status: AccessStatus,
    val createdAt: Instant
)
