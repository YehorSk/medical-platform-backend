package com.yehorsk.medicalplatformbackend.appointments.mappers

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentResponseDto
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun AppointmentEntity.toAppointmentResponseDto(role: UserRole): AppointmentResponseDto {
    val zonedDateTime = this.dateTime.atZone(ZoneId.systemDefault())

    return when(role) {
        UserRole.DOCTOR -> {
            AppointmentResponseDto(
                id = this.id!!,
                patient = this.patient.toUserResponseDto(),
                status = this.status,
                note = this.note,
                date = zonedDateTime.toLocalDate(),
                time = zonedDateTime.toLocalTime(),
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
            )
        }
        UserRole.PATIENT -> {
            AppointmentResponseDto(
                id = this.id!!,
                doctor = this.doctor.toUserResponseDto(),
                specialization = this.doctor.doctor?.specialization?.name ?: "",
                status = this.status,
                note = this.note,
                date = zonedDateTime.toLocalDate(),
                time = zonedDateTime.toLocalTime(),
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
            )
        }
        UserRole.ADMIN -> {
            AppointmentResponseDto(
                id = this.id!!,
                doctor = this.doctor.toUserResponseDto(),
                patient = this.patient.toUserResponseDto(),
                specialization = this.doctor.doctor?.specialization?.name ?: "",
                status = this.status,
                note = this.note,
                date = zonedDateTime.toLocalDate(),
                time = zonedDateTime.toLocalTime(),
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
            )
        }
    }
}

fun LocalDate.toInstantAtTime(time: LocalTime): Instant {
    return this.atTime(time)
        .atZone(ZoneId.systemDefault())
        .toInstant()
}
