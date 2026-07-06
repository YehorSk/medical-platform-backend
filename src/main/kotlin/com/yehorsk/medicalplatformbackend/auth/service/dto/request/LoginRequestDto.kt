package com.yehorsk.medicalplatformbackend.auth.service.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
)
