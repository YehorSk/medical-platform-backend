package com.yehorsk.medicalplatformbackend.medical_card.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId

data class MedicalCardPatientDto(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val title: String
)