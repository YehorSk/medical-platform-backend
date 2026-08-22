package com.yehorsk.medicalplatformbackend.chat.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import java.time.Instant

data class ConversationResponseDto(
    val id: ConversationId,
    val patient: ParticipantDto,
    val doctor: ParticipantDto,
    val lastMessageAt: Instant? = null,
    val lastMessage: MessageResponseDto? = null
)