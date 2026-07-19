package com.yehorsk.medicalplatformbackend.doctor.service.dto.request

import jakarta.validation.constraints.NotBlank

data class CreateSpecializationRequestDto(
    @field:NotBlank(message = "Name is required")
    val name: String
)

