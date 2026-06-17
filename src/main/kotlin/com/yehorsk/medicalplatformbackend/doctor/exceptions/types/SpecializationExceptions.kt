package com.yehorsk.medicalplatformbackend.doctor.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class SpecializationDoesNotExist : AppException("SPECIALIZATION_NOT_FOUND", HttpStatus.NOT_FOUND, "Specialization does not exist")
