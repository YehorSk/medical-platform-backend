package com.yehorsk.medicalplatformbackend.user.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.domain.response.ErrorResponse
import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.user.exceptions.types.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(EmailIsTakenException::class)
    fun onEmailIsTaken(e: EmailIsTakenException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidCredentialsException::class)
    fun onInvalidCredentials(e: InvalidCredentialsException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(UserDoesNotExistException::class)
    fun onUserDoesNotExist(e: UserDoesNotExistException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(UserAlreadyExistException::class)
    fun onUserAlreadyExist(e: UserAlreadyExistException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(DoctorAlreadyExistException::class)
    fun onDoctorAlreadyExist(e: DoctorAlreadyExistException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(DoctorNotApprovedException::class)
    fun onDoctorNotApproved(e: DoctorNotApprovedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(UserNotAuthenticatedException::class)
    fun onUserNotAuthenticated(e: UserNotAuthenticatedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun onInvalidRefreshToken(e: InvalidRefreshTokenException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidAccessTokenException::class)
    fun onInvalidAccessToken(e: InvalidAccessTokenException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidTokenException::class)
    fun onInvalidToken(e: InvalidTokenException, request: HttpServletRequest) =
        e.toResponse(request)

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
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                status = 400,
                errorCode = "VALIDATION_ERROR",
                message = "Validation failed",
                path = request.requestURI,
                errors = errors
            )
        )
    }

}