package com.yehorsk.medicalplatformbackend.user.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class DoctorAlreadyExistException : AppException("DOCTOR_EXISTS", HttpStatus.CONFLICT, "Doctor already exists")
class DoctorNotApprovedException : AppException("DOCTOR_NOT_APPROVED", HttpStatus.FORBIDDEN, "Doctor is not approved")
class EmailIsTakenException : AppException("EMAIL_TAKEN", HttpStatus.CONFLICT, "Email is already taken")
class InvalidAccessTokenException : AppException("INVALID_ACCESS_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid access token")
class InvalidCredentialsException : AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid credentials")
class InvalidRefreshTokenException : AppException("INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid refresh token")
class InvalidTokenException : AppException("INVALID_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid token")
class UserAlreadyExistException : AppException("USER_EXISTS", HttpStatus.CONFLICT, "User already exists")
class UserDoesNotExistException : AppException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User does not exist")
class UserNotAuthenticatedException : AppException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Not authenticated")