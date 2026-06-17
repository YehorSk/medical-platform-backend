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

    @ExceptionHandler(UserNotAuthenticatedException::class)
    fun onUserNotAuthenticated(e: UserNotAuthenticatedException, request: HttpServletRequest) =
        e.toResponse(request)

}