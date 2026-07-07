package com.yehorsk.medicalplatformbackend.user.service.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateUserRequestDto(
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    @field:NotBlank(message = "Last name is required")
    val lastName: String,
    val title: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactPhone: String? = null,
)

