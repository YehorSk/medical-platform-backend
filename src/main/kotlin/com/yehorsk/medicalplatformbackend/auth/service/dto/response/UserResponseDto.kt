package com.yehorsk.medicalplatformbackend.auth.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorResponseDto

data class UserResponseDto(
    val id: UserId,
    val email : String,
    val doctor: DoctorResponseDto ?= null,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val phone: String = "",
    val address: String = "",
    val title: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = ""
)
