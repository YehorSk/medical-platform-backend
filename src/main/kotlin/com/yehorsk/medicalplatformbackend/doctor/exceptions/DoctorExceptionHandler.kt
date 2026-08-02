package com.yehorsk.medicalplatformbackend.doctor.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DoctorExceptionHandler {

    @ExceptionHandler(DoctorAlreadyExistException::class)
    fun onDoctorAlreadyExist(e: DoctorAlreadyExistException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(DoctorNotApprovedException::class)
    fun onDoctorNotApproved(e: DoctorNotApprovedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(DoctorDoesNotExistException::class)
    fun onDoctorDoeNotExist(e: DoctorDoesNotExistException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidScheduleException::class)
    fun onInvalidSchedule(e: InvalidScheduleException, request: HttpServletRequest) =
        e.toResponse(request)


}

