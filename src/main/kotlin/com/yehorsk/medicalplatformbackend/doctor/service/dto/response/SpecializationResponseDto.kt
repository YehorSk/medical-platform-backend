package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId

data class SpecializationResponseDto(
    val id: SpecializationId?,
    val name: String
)

