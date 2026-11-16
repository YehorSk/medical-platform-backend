package com.yehorsk.medicalplatformbackend.notification.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException

import org.springframework.http.HttpStatus

class InvalidDeviceTokenException: AppException("INVALID_DEVICE_TOKEN", HttpStatus.BAD_REQUEST, "Device token is not a valid Firebase token")
