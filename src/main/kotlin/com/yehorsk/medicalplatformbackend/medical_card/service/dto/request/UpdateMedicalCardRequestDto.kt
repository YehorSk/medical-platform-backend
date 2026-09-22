package com.yehorsk.medicalplatformbackend.medical_card.service.dto.request

import com.yehorsk.medicalplatformbackend.medical_card.database.entity.BloodType
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.Gender
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.InsuranceCompany
import jakarta.validation.constraints.Pattern

data class UpdateMedicalCardRequestDto(
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "dateOfBirth must be in format yyyy-MM-dd")
    val dateOfBirth: String? = null,
    val bloodType: BloodType? = null,
    val gender: Gender? = null,
    val insuranceCompany: InsuranceCompany? = null,
    val insuranceNumber: String? = null,
)


