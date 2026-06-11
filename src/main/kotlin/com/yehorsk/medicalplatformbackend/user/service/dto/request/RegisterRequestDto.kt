package com.yehorsk.medicalplatformbackend.user.service.dto.request

import com.yehorsk.medicalplatformbackend.user.service.validation.PasswordMatches
import com.yehorsk.medicalplatformbackend.user.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.user.service.validation.RoleMatches
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

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
    val role: UserRole,
    val licenseNumber: String? = null
)
