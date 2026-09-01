package com.yehorsk.medicalplatformbackend.medical_card.service.mappers

import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardResponseDto
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.toMedicalCardPatientDto

fun MedicalCardEntity.toMedicalCardResponseDto() = MedicalCardResponseDto(
    id = id!!,
    bloodType = bloodType,
    gender = gender,
    dateOfBirth = dateOfBirth.toString(),
    insuranceNumber = insuranceNumber,
    patient = patient?.toMedicalCardPatientDto(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

