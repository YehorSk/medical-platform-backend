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
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(EmailIsTakenException::class)
    fun handleEmailTaken(e: EmailIsTakenException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(mapOf("email" to "Email is already taken"))
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(e: InvalidCredentialsException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "Invalid credentials"))
    }

    @ExceptionHandler(UserDoesNotExistException::class)
    fun handleUserDoesNotExist(e: UserDoesNotExistException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "User does not exist"))
    }

    @ExceptionHandler(UserAlreadyExistException::class)
    fun handleUserAlreadyExist(e: UserAlreadyExistException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "User already exist"))
    }

    @ExceptionHandler(DoctorAlreadyExistException::class)
    fun handleDoctorAlreadyExist(e: DoctorAlreadyExistException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "Doctor already exist"))
    }

    @ExceptionHandler(DoctorNotApprovedException::class)
    fun handleDoctorNotApproved(e: DoctorNotApprovedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to "Doctor not approved"))
    }

    @ExceptionHandler(UserNotAuthenticatedException::class)
    fun userNotAuthenticated(e: UserNotAuthenticatedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to "User isn't authenticated."))
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun invalidRefreshToken(e: InvalidRefreshTokenException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to "Invalid refresh token"))
    }

    @ExceptionHandler(InvalidAccessTokenException::class)
    fun invalidAccessToken(e: InvalidAccessTokenException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("error" to "Invalid access token"))
    }

}