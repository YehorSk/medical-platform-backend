package com.yehorsk.medicalplatformbackend.notification.infra.notifications.model

import java.util.UUID

data class PushNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val recipients: List<DeviceToken>,
    val message: String,
    // Android replaces an older unread notification with the same key instead of stacking them
    val collapseKey: String,
    val data: Map<String, String>
)