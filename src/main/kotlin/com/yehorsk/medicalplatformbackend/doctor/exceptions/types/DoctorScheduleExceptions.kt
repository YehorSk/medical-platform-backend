package com.yehorsk.medicalplatformbackend.doctor.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class InvalidScheduleException(message: String): AppException("INVALID_SCHEDULE", HttpStatus.BAD_REQUEST, message)

class ScheduleConflictsWithAppointmentsException(count: Int): AppException(
    "SCHEDULE_CONFLICTS_WITH_APPOINTMENTS",
    HttpStatus.CONFLICT,
    "New schedule conflicts with $count upcoming appointment(s). Cancel or reschedule them first"
)
