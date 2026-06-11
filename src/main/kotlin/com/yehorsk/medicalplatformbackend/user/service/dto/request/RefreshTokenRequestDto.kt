package com.yehorsk.medicalplatformbackend.user.service.dto.request

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequestDto(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
)
