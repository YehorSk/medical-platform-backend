package com.yehorsk.medicalplatformbackend.appointments.service.dto.response

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class AdminAppointmentResponseDto(
    val id: AppointmentId,
    val doctor: AppointmentDoctorDto,
    val patient: AppointmentPatientDto,
    val status: AppointmentStatus,
    val note: String,
    val date: LocalDate,
    val time: LocalTime,
    val createdAt: Instant,
    val updatedAt: Instant
) : AppointmentResponseDto
