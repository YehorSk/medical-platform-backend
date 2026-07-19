package com.yehorsk.medicalplatformbackend.doctor.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId

data class GetDoctorsWithFilterDto(
    val search: String? = null,
    val specializations: List<SpecializationId>? = null,
    val city: String? = null
)
