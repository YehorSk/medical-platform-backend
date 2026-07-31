package com.yehorsk.medicalplatformbackend.common.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.domain.response.ErrorResponse
import com.yehorsk.medicalplatformbackend.common.exceptions.types.RateLimitException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = e.bindingResult.fieldErrors.map { fe ->
            ErrorResponse.FieldError(
                field = fe.field,
                message = fe.defaultMessage ?: "Invalid value"
            )
        }

        val objectErrors = e.bindingResult.globalErrors.map { oe ->
            ErrorResponse.FieldError(
                field = oe.objectName,
                message = oe.defaultMessage ?: "Invalid value"
            )
        }

        val errors = fieldErrors + objectErrors
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
            ErrorResponse(
                status = 422,
                errorCode = "VALIDATION_ERROR",
                message = "Validation failed",
                path = request.requestURI,
                errors = errors
            )
        )
    }

    @ExceptionHandler(RateLimitException::class)
    fun onRateLimitException(
        e: RateLimitException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
            ErrorResponse(
                status = 429,
                errorCode = "RATE_LIMIT_EXCEEDED",
                message = "Rate limit exceeded",
                path = request.requestURI,
                errors = emptyList()
            )
        )
    }

}