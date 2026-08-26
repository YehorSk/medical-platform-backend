package com.yehorsk.medicalplatformbackend.notification.mappers

import com.yehorsk.medicalplatformbackend.notification.controller.dto.DeviceTokenDto
import com.yehorsk.medicalplatformbackend.notification.controller.dto.PlatformDto
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.DeviceTokenEntity
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.PlatformEntity
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.DeviceToken

fun DeviceToken.toDeviceTokenDto(): DeviceTokenDto {
    return DeviceTokenDto(
        userId = userId,
        token = token,
        createdAt = createdAt
    )
}

fun PlatformDto.toPlatformDto(): PlatformEntity {
    return when(this) {
        PlatformDto.ANDROID -> PlatformEntity.ANDROID
    }
}