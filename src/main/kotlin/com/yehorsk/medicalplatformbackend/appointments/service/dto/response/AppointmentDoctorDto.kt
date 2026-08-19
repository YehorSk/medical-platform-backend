package com.yehorsk.medicalplatformbackend.appointments.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId

data class AppointmentDoctorDto(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val title: String,
    val specialization: String
)