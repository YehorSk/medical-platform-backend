package com.yehorsk.medicalplatformbackend.chat.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId

data class SendMessageRequestDto(
    val conversationId: ConversationId,
    val content: String
)
