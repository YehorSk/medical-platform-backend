package com.yehorsk.medicalplatformbackend.auth.service.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email address")
    val email: String
)