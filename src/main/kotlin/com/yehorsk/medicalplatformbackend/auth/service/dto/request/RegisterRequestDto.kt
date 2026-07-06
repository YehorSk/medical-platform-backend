package com.yehorsk.medicalplatformbackend.auth.service.dto.request

import com.yehorsk.medicalplatformbackend.auth.service.validation.PasswordMatches
import com.yehorsk.medicalplatformbackend.auth.service.validation.RoleMatches
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@PasswordMatches
@RoleMatches
data class RegisterRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
    @field:NotBlank(message = "Password confirmation is required")
    val passwordConfirm: String,
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    @field:NotBlank(message = "Last name is required")
    val lastName: String,
    @field:NotBlank(message = "Phone is required")
    val phone: String,
    @field:NotNull(message = "Role is required")
    var role: String,
    val licenseNumber: String? = null
)
