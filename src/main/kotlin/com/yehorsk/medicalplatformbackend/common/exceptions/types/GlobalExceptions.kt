package com.yehorsk.medicalplatformbackend.common.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class AccessDeniedException : AppException("ACCESS_DENIED", HttpStatus.FORBIDDEN, "Access denied")
class RateLimitException(resetsInSeconds: Long) : AppException("RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Please try again in $resetsInSeconds seconds.")