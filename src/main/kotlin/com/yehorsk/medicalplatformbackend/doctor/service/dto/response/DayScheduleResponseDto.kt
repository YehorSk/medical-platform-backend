package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import java.time.LocalTime

data class DayScheduleResponseDto(
    val weekday: WeekDay,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val lunchStart: LocalTime?,
    val lunchEnd: LocalTime?,
    val isWorkingDay: Boolean,
    val slotDurationMinutes: Int,
    val breakBetweenMinutes: Int
)