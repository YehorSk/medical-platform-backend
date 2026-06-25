package com.yehorsk.medicalplatformbackend.user.service.mappers

import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.user.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.user.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.user.service.dto.response.UserResponseDto

fun UserEntity.toUserResponseDto() = UserResponseDto(
    id = id!!,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = role
)

fun String.toUserRole(): UserRole{
    return when(this.trim().uppercase()){
        "PATIENT" -> UserRole.PATIENT
        "DOCTOR" -> UserRole.DOCTOR
        else -> UserRole.PATIENT
    }
}