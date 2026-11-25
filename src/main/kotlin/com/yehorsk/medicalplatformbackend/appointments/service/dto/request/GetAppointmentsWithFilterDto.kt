package com.yehorsk.medicalplatformbackend.appointments.service.dto.request

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import org.springframework.data.domain.Sort
import java.time.Instant

data class GetAppointmentsWithFilterDto(
    val statuses: List<AppointmentStatus>? = null,
    val doctorId: DoctorId? = null,
    val patientId: UserId? = null,
    val from: Instant? = null,
    val to: Instant? = null,
    val upcoming: Boolean = false,
    val sortDirection: Sort.Direction = Sort.Direction.ASC
)
