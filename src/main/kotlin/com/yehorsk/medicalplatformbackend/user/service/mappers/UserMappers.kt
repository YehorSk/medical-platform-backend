package com.yehorsk.medicalplatformbackend.user.service.mappers

import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.user.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.user.service.dto.response.UserResponseDto

fun UserEntity.toUserResponseDto() = UserResponseDto(
    id = id!!,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = role
)