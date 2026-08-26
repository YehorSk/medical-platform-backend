package com.yehorsk.medicalplatformbackend.notification.controller.dto

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant

data class DeviceTokenDto(
    val userId: UserId,
    val token: String,
    val createdAt: Instant
)