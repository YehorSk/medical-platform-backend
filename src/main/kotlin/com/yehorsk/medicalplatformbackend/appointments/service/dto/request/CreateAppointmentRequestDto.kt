package com.yehorsk.medicalplatformbackend.appointments.service.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.LocalDate
import java.time.LocalTime
import jakarta.validation.constraints.NotNull

data class CreateAppointmentRequestDto(
    @field:NotNull
    var doctorId: UserId,

    @field:NotNull
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    var date: LocalDate,

    @field:NotNull
    @field:JsonFormat(pattern = "HH:mm")
    var time: LocalTime,

    var note: String = ""
)


