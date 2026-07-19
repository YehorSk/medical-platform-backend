package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.ClinicId

data class ClinicResponseDto(
    val id: ClinicId?,
    val name: String,
    val address: String,
    val phone: String,
    val city: String
)

