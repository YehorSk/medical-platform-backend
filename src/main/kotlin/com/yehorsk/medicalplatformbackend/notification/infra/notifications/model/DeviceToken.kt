package com.yehorsk.medicalplatformbackend.notification.infra.notifications.model

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.PlatformEntity
import java.time.Instant

data class DeviceToken(
    val id: Long,
    val userId: UserId,
    val token: String,
    val platform: PlatformEntity,
    val createdAt: Instant = Instant.now(),
)