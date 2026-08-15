package com.yehorsk.medicalplatformbackend.appointments.service.dto.response

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorResponseDto
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class AppointmentResponseDto(
    val id: AppointmentId,
    val doctor: UserResponseDto? = null,
    val patient: UserResponseDto? = null,
    val specialization: String = "",
    val status: AppointmentStatus,
    val note: String,
    val date: LocalDate,
    val time: LocalTime,
    val createdAt: Instant,
    val updatedAt: Instant
)


