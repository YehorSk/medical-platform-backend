package com.yehorsk.medicalplatformbackend.auth.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class EmailIsTakenException : AppException("EMAIL_TAKEN", HttpStatus.CONFLICT, "Email is already taken")
class InvalidCredentialsException : AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid credentials")
class InvalidTokenException : AppException("INVALID_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid token")
class UserAlreadyExistException : AppException("USER_EXISTS", HttpStatus.CONFLICT, "User already exists")
class UserDoesNotExistException : AppException("USER_NOT_FOUND", HttpStatus.UNAUTHORIZED, "User does not exist")
class UserNotAuthenticatedException : AppException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Not authenticated")
class InvalidResetCodeException : AppException("INVALID_RESET_CODE",HttpStatus.BAD_REQUEST,"The reset code is invalid")
class ExpiredResetCodeException : AppException("EXPIRED_RESET_CODE",HttpStatus.BAD_REQUEST,"The reset code has expired")
class TooManyAttemptsException : AppException("TOO_MANY_ATTEMPTS",HttpStatus.TOO_MANY_REQUESTS,"Too many failed attempts, please request a new code")
class TooManyRequestsException : AppException("TOO_MANY_REQUESTS",HttpStatus.TOO_MANY_REQUESTS,"Too many requests, please try again later")
class EmailNotVerifiedException : AppException("EMAIL_NOT_VERIFIED",HttpStatus.FORBIDDEN,"Email is not verified")