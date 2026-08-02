package com.yehorsk.medicalplatformbackend.doctor.service.dto

import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.jetbrains.annotations.NotNull
import java.time.LocalTime

data class DayScheduleDto(
    @field:NotNull()
    val weekday: WeekDay,

    val isWorkingDay: Boolean,

    val startTime: LocalTime? = null,

    val endTime: LocalTime? = null,

    val lunchStart: LocalTime? = null,

    val lunchEnd: LocalTime? = null,

    @field:Positive(message = "Slot duration must be positive")
    val slotDurationMinutes: Int? = 30,

    @field:PositiveOrZero(message = "Break between minutes cannot be negative")
    val breakBetweenMinutes: Int? = 0
)