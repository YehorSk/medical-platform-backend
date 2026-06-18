package com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.AccessRequestAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class PatientHasDoctorExceptionHandler {

    @ExceptionHandler(AccessRequestAlreadyExistsException::class)
    fun onAccessRequestAlreadyExist(e: AccessRequestAlreadyExistsException, request: HttpServletRequest) =
        e.toResponse(request)

}