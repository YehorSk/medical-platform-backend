package com.yehorsk.medicalplatformbackend.user.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class EmailIsTakenException : AppException("EMAIL_TAKEN", HttpStatus.CONFLICT, "Email is already taken")
class InvalidCredentialsException : AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid credentials")
class InvalidTokenException : AppException("INVALID_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid token")
class UserAlreadyExistException : AppException("USER_EXISTS", HttpStatus.CONFLICT, "User already exists")
class UserDoesNotExistException : AppException("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User does not exist")
class UserNotAuthenticatedException : AppException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Not authenticated")