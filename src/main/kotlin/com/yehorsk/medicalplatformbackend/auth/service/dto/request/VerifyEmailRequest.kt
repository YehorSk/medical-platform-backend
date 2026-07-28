package com.yehorsk.medicalplatformbackend.auth.service.dto.request

import jakarta.validation.constraints.NotBlank

data class VerifyEmailRequest(
    @field:NotBlank(message = "Verification token is required")
    val token: String
)