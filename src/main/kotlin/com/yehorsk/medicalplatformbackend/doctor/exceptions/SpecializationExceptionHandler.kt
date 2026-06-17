package com.yehorsk.medicalplatformbackend.doctor.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SpecializationExceptionHandler {

    @ExceptionHandler(SpecializationDoesNotExist::class)
    fun onUserDoesNotExist(e: SpecializationDoesNotExist, request: HttpServletRequest) =
        e.toResponse(request)

}