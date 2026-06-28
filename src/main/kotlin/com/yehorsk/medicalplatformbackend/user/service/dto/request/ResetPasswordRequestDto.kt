package com.yehorsk.medicalplatformbackend.user.service.dto.request

import com.yehorsk.medicalplatformbackend.user.service.validation.PasswordMatches
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@PasswordMatches
data class ResetPasswordRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
    @field:NotBlank(message = "Password confirmation is required")
    val passwordConfirm: String,
    @field:NotBlank(message = "Code is required")
    val code: String,
)