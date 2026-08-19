package com.yehorsk.medicalplatformbackend.appointments.mappers

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AdminAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentDoctorDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentPatientDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.DoctorAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.PatientAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun LocalDate.toInstantAtTime(time: LocalTime): Instant {
    return this.atTime(time)
        .atZone(ZoneId.systemDefault())
        .toInstant()
}

fun UserEntity.toAppointmentPatientDto() = AppointmentPatientDto(
    id = id!!,
    firstName = firstName,
    lastName = lastName,
    title = title ?: ""
)

fun UserEntity.toAppointmentDoctorDto() = AppointmentDoctorDto(
    id = doctor!!.id!!,
    firstName = firstName,
    lastName = lastName,
    title = title ?: "",
    specialization = doctor?.specialization?.name ?: ""
)

fun AppointmentEntity.toDoctorAppointmentResponseDto(): DoctorAppointmentResponseDto {
    val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())

    return DoctorAppointmentResponseDto(
        id = id!!,
        doctor = doctor.toAppointmentDoctorDto(),
        patient = patient.toAppointmentPatientDto(),
        status = status,
        note = note,
        date = zonedDateTime.toLocalDate(),
        time = zonedDateTime.toLocalTime(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun AppointmentEntity.toPatientAppointmentResponseDto(): PatientAppointmentResponseDto {
    val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())

    return PatientAppointmentResponseDto(
        id = id!!,
        doctor = doctor.toAppointmentDoctorDto(),
        status = status,
        note = note,
        date = zonedDateTime.toLocalDate(),
        time = zonedDateTime.toLocalTime(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun AppointmentEntity.toAdminAppointmentResponseDto(): AdminAppointmentResponseDto {
    val zonedDateTime = dateTime.atZone(ZoneId.systemDefault())

    return AdminAppointmentResponseDto(
        id = id!!,

        doctor = doctor.toAppointmentDoctorDto(),

        patient = patient.toAppointmentPatientDto(),

        status = status,
        note = note,

        date = zonedDateTime.toLocalDate(),
        time = zonedDateTime.toLocalTime(),

        createdAt = createdAt,
        updatedAt = updatedAt
    )
}