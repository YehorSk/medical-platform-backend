package com.yehorsk.medicalplatformbackend.appointments.service.dto.request

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId

data class UpdateAppointmentStatusRequestDto(
    val appointmentId: AppointmentId,
    val status: AppointmentStatus,
    val note: String = ""
)

