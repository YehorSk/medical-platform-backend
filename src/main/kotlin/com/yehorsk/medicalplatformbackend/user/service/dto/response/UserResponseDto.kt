package com.yehorsk.medicalplatformbackend.user.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.user.database.entity.UserRole

data class UserResponseDto(
    val id: UserId,
    val email : String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
)
