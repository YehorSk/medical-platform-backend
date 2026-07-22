package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto
import java.time.Instant

data class DoctorResponseDto(
    val id: DoctorId?,
    val licenseNumber: String,
    val createdAt: Instant,
    val user: UserResponseDto? = null,
    val approvedBy: UserResponseDto? = null,
    val approved: Boolean = false,
    val description: String = "",
    val specialization: SpecializationResponseDto? = null,
    val workplace: WorkplaceResponseDto? = null,
    val updatedAt: Instant? = null,
    val approvedAt: Instant? = null,
    val currentPatientHasDoctor: Boolean = false
)

