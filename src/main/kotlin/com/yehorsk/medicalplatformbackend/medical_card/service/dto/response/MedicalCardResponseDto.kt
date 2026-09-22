package com.yehorsk.medicalplatformbackend.medical_card.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.BloodType
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.Gender
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.InsuranceCompany
import java.time.Instant

data class MedicalCardResponseDto(
    val id: MedicalCardId,
    val dateOfBirth: String = "",
    val bloodType: BloodType? = null,
    val gender: Gender? = null,
    val insuranceProvider: InsuranceCompany? = null,
    val insuranceNumber: String = "",
    val patient: MedicalCardPatientDto? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

