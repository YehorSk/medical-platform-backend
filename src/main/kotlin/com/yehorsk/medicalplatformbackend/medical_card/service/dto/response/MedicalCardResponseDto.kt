package com.yehorsk.medicalplatformbackend.medical_card.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.BloodType
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.Gender
import java.time.Instant

data class MedicalCardResponseDto(
    val id: MedicalCardId,
    val dateOfBirth: String = "",
    val bloodType: BloodType? = null,
    val gender: Gender? = null,
    val insuranceNumber: String? = null,
    val patient: MedicalCardPatientDto? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

