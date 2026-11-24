package com.yehorsk.medicalplatformbackend.notification.infra.notifications.model

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId

// In-process Spring event for clients without FCM (desktop), delivered over their open WebSocket sessions
data class UserNotificationEvent(
    val userIds: Collection<UserId>,
    val title: String,
    val body: String,
    val data: Map<String, String>
)
