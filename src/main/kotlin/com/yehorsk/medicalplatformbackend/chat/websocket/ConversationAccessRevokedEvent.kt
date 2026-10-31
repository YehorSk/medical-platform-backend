package com.yehorsk.medicalplatformbackend.chat.websocket

import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId

// In-process Spring event, handled by ChatWebSocketHandler after the revoke transaction commits
data class ConversationAccessRevokedEvent(
    val conversationId: ConversationId,
    val participantIds: List<UserId>
)
