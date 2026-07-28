package com.yehorsk.medicalplatformbackend.auth.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.*
import jakarta.servlet.http.HttpServletRequest
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

    @ExceptionHandler(UserNotAuthenticatedException::class)
    fun onUserNotAuthenticated(e: UserNotAuthenticatedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidResetCodeException::class)
    fun onInvalidResetCode(e: InvalidResetCodeException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(ExpiredResetCodeException::class)
    fun onExpiredResetCode(e: ExpiredResetCodeException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(TooManyAttemptsException::class)
    fun onTooManyAttempts(e: TooManyAttemptsException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(TooManyRequestsException::class)
    fun onTooManyRequests(e: TooManyRequestsException, request: HttpServletRequest) =
        e.toResponse(request)

}