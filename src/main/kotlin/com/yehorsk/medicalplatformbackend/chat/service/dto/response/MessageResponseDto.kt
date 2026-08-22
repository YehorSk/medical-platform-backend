package com.yehorsk.medicalplatformbackend.chat.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.MessageId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant

data class MessageResponseDto(
    val id: MessageId,
    val conversationId: ConversationId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
