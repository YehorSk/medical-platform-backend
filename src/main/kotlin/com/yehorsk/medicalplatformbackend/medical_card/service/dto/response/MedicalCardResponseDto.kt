package com.yehorsk.medicalplatformbackend.medical_card.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import java.time.Instant

data class MedicalCardResponseDto(
    val id: MedicalCardId,
    val bloodType: String = "",
    val insuranceNumber: String? = null,
    val user: MedicalCardPatientDto? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

