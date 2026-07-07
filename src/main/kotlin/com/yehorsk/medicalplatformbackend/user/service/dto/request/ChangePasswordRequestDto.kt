package com.yehorsk.medicalplatformbackend.user.service.dto.request

import com.yehorsk.medicalplatformbackend.auth.service.validation.PasswordMatches
import jakarta.validation.constraints.NotBlank

@PasswordMatches
data class ChangePasswordRequestDto(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    @field:NotBlank(message = "New password is required")
    val password: String,
    @field:NotBlank(message = "Password confirmation is required")
    val passwordConfirm: String,
)

