package com.yehorsk.medicalplatformbackend.appointments.mappers

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentResponseDto
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun AppointmentEntity.toAppointmentResponseDto(): AppointmentResponseDto {
    val zonedDateTime = this.dateTime.atZone(ZoneId.systemDefault())
    
    return AppointmentResponseDto(
        id = this.id!!,
        doctorId = this.doctor.id!!,
        doctorName = "${this.doctor.firstName} ${this.doctor.lastName}",
        patientId = this.patient.id!!,
        patientName = "${this.patient.firstName} ${this.patient.lastName}",
        status = this.status,
        note = this.note,
        date = zonedDateTime.toLocalDate(),
        time = zonedDateTime.toLocalTime(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun LocalDate.toInstantAtTime(time: LocalTime): Instant {
    return this.atTime(time)
        .atZone(ZoneId.systemDefault())
        .toInstant()
}


