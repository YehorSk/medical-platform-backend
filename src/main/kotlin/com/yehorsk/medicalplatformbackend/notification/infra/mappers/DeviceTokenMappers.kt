package com.yehorsk.medicalplatformbackend.notification.infra.mappers

import com.yehorsk.medicalplatformbackend.notification.infra.database.model.DeviceTokenEntity
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.DeviceToken

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        userId = userId,
        token = token,
        platform = platform,
        createdAt = createdAt,
        id = id
    )
}