package com.yehorsk.medicalplatformbackend.notification.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.notification.exceptions.types.InvalidDeviceTokenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DeviceTokenExceptionHandler {

    @ExceptionHandler(InvalidDeviceTokenException::class)
    fun onInvalidDeviceToken(e: InvalidDeviceTokenException, request: HttpServletRequest) =
        e.toResponse(request)

}
