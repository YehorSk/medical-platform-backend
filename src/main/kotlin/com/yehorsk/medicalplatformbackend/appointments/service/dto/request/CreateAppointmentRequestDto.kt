package com.yehorsk.medicalplatformbackend.appointments.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.LocalDate
import java.time.LocalTime

data class CreateAppointmentRequestDto(
    val doctorId: UserId,
    val date: LocalDate,
    val time: LocalTime,
    val note: String = ""
)


