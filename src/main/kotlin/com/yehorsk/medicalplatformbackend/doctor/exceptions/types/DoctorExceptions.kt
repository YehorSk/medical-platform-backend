package com.yehorsk.medicalplatformbackend.doctor.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class DoctorAlreadyExistException : AppException("DOCTOR_EXISTS", HttpStatus.CONFLICT, "Doctor already exists")
class DoctorNotApprovedException : AppException("DOCTOR_NOT_APPROVED", HttpStatus.FORBIDDEN, "Doctor is not approved")
class DoctorDoesNotExistException : AppException("DOCTOR_NOT_EXIST", HttpStatus.NOT_FOUND, "Doctor does not exist")