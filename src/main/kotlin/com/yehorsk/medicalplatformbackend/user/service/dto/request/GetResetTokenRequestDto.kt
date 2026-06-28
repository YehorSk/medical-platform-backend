package com.yehorsk.medicalplatformbackend.user.service.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class GetResetTokenRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email
    val email: String,
)
