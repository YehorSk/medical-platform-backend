package com.yehorsk.medicalplatformbackend.medical_card.service.mappers

import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto

fun MedicalCardEntity.toMedicalCardResponseDto() = MedicalCardResponseDto(
    id = id!!,
    bloodType = bloodType ?: "",
    insuranceNumber = insuranceNumber,
    user = user?.toUserResponseDto(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

