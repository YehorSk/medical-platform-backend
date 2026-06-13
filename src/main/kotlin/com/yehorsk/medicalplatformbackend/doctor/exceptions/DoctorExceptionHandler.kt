package com.yehorsk.medicalplatformbackend.doctor.exceptions

import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorForbiddenException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorNotAuthenticatedException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorNotFoundException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorUserNotFoundException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.SpecializationNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
// ...existing code...
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DoctorExceptionHandler {

    @ExceptionHandler(DoctorNotAuthenticatedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onDoctorNotAuthenticated(e: DoctorNotAuthenticatedException) = mapOf(
        "code" to "UNAUTHORIZED",
        "message" to e.message
    )

    @ExceptionHandler(DoctorForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun onDoctorForbidden(e: DoctorForbiddenException) = mapOf(
        "code" to "FORBIDDEN",
        "message" to e.message
    )

    @ExceptionHandler(DoctorUserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onDoctorUserNotFound(e: DoctorUserNotFoundException) = mapOf(
        "code" to "USER_NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(DoctorNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onDoctorNotFound(e: DoctorNotFoundException) = mapOf(
        "code" to "DOCTOR_NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(SpecializationNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onSpecializationNotFound(e: SpecializationNotFoundException) = mapOf(
        "code" to "SPECIALIZATION_NOT_FOUND",
        "message" to e.message
    )

}

