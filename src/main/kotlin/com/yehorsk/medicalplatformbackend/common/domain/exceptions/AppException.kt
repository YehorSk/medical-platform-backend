package com.yehorsk.medicalplatformbackend.common.domain.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.domain.response.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

open class AppException(
    val errorCode: String,
    val httpStatus: HttpStatus,
    message: String
) : RuntimeException(message)

fun AppException.toResponse(request: HttpServletRequest) =
    ResponseEntity
        .status(httpStatus)
        .body(
            ErrorResponse(
                status = httpStatus.value(),
                errorCode = errorCode,
                message = message!!,
                path = request.requestURI
            )
        )