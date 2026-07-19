package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.WorkplaceId

data class WorkplaceResponseDto(
    val id: WorkplaceId?,
    val roomNumber: String,
    val clinic: ClinicResponseDto
)

