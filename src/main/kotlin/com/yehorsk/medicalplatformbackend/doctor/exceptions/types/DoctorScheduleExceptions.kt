package com.yehorsk.medicalplatformbackend.doctor.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class InvalidScheduleException(message: String): AppException("INVALID_SCHEDULE", HttpStatus.CONFLICT, "Invalid doctor schedule")