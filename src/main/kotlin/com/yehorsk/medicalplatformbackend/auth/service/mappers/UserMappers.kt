package com.yehorsk.medicalplatformbackend.auth.service.mappers

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto

fun UserEntity.toUserResponseDto() = UserResponseDto(
    id = id!!,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = role,
    phone = phone ?: "",
    address = address ?: "",
    title = title ?: "",
    emergencyContactName = emergencyContactName ?: "",
    emergencyContactPhone = emergencyContactPhone ?: "",
)

fun String.toUserRole(): UserRole{
    return when(this.trim().uppercase()){
        "PATIENT" -> UserRole.PATIENT
        "DOCTOR" -> UserRole.DOCTOR
        else -> UserRole.PATIENT
    }
}