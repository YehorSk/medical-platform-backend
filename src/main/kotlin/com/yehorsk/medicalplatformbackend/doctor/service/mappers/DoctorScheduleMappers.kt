package com.yehorsk.medicalplatformbackend.doctor.service.mappers

import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorScheduleEntity
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DayScheduleResponseDto

fun DoctorScheduleEntity.toDayScheduleResponseDto() = DayScheduleResponseDto(
    weekday = this.weekDay!!,
    startTime = this.startTime!!,
    endTime = this.endTime!!,
    lunchStart = this.lunchStart,
    lunchEnd = this.lunchEnd,
    isWorkingDay = this.isWorkingDay,
    slotDurationMinutes = this.slotDurationMinutes,
    breakBetweenMinutes = this.breakBetweenMinutes
)