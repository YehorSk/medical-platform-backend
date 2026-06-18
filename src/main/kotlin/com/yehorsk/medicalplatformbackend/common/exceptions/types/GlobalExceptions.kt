package com.yehorsk.medicalplatformbackend.common.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class AccessDeniedException : AppException("ACCESS_DENIED", HttpStatus.FORBIDDEN, "Access denied")