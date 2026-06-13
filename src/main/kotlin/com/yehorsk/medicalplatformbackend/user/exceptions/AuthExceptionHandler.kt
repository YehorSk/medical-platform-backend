package com.yehorsk.medicalplatformbackend.user.exceptions

import com.yehorsk.medicalplatformbackend.user.exceptions.types.DoctorAlreadyExistException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.DoctorNotApprovedException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.EmailIsTakenException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.InvalidAccessTokenException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.InvalidRefreshTokenException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserAlreadyExistException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserNotAuthenticatedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(EmailIsTakenException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onEmailIsTaken(e: EmailIsTakenException) = mapOf(
        "code" to "EMAIL_TAKEN",
        "message" to e.message
    )

    @ExceptionHandler(InvalidCredentialsException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onInvalidCredentials(e: InvalidCredentialsException) = mapOf(
        "code" to "INVALID_CREDENTIALS",
        "message" to e.message
    )

    @ExceptionHandler(UserDoesNotExistException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onUserDoesNotExist(e: UserDoesNotExistException) = mapOf(
        "code" to "USER_NOT_FOUND",
        "message" to e.message
    )

    @ExceptionHandler(UserAlreadyExistException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onUserAlreadyExist(e: UserAlreadyExistException) = mapOf(
        "code" to "USER_EXISTS",
        "message" to e.message
    )

    @ExceptionHandler(DoctorAlreadyExistException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onDoctorAlreadyExist(e: DoctorAlreadyExistException) = mapOf(
        "code" to "DOCTOR_EXISTS",
        "message" to e.message
    )

    @ExceptionHandler(DoctorNotApprovedException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onDoctorNotApproved(e: DoctorNotApprovedException) = mapOf(
        "code" to "DOCTOR_NOT_APPROVED",
        "message" to e.message
    )

    @ExceptionHandler(UserNotAuthenticatedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onUserNotAuthenticated(e: UserNotAuthenticatedException) = mapOf(
        "code" to "UNAUTHORIZED",
        "message" to e.message
    )

    @ExceptionHandler(InvalidRefreshTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onInvalidRefreshToken(e: InvalidRefreshTokenException) = mapOf(
        "code" to "INVALID_REFRESH_TOKEN",
        "message" to e.message
    )

    @ExceptionHandler(InvalidAccessTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onInvalidAccessToken(e: InvalidAccessTokenException) = mapOf(
        "code" to "INVALID_ACCESS_TOKEN",
        "message" to e.message
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onValidationException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult!!.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "code" to "VALIDATION_ERROR",
                    "errors" to errors
                )
            )
    }

}