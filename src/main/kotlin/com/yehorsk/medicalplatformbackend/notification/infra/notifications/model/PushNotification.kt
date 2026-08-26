package com.yehorsk.medicalplatformbackend.notification.infra.notifications.model

import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import java.util.UUID

data class PushNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val recipients: List<DeviceToken>,
    val message: String,
    val conversationId: ConversationId,
    val data: Map<String, String>
)