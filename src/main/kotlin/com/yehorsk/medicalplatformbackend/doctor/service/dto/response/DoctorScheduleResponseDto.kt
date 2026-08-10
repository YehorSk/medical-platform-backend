package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

data class DoctorScheduleResponseDto(
    val doctor: DoctorResponseDto,
    val daySchedule: List<DayScheduleResponseDto>
)