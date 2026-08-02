package com.yehorsk.medicalplatformbackend.doctor.service.dto.request

import com.yehorsk.medicalplatformbackend.doctor.service.dto.DayScheduleDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

data class UpdateScheduleRequestDto(
    @field:NotEmpty(message = "Schedules list cannot be empty")
    @field:Valid
    val schedules: List<DayScheduleDto>
)