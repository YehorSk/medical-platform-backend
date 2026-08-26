package com.yehorsk.medicalplatformbackend.common.domain.events.conversation

import com.yehorsk.medicalplatformbackend.common.domain.events.MedConnectEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant
import java.util.UUID

sealed class ConversationEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = ConversationEventConstants.CONVERSATION_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
): MedConnectEvent{

    data class NewMessage(
        val senderId: UserId,
        val recipient: UserId,
        val senderUsername: String,
        val conversationId: ConversationId,
        val content: String,
        override val eventKey: String = ConversationEventConstants.CONVERSATION_NEW_MESSAGE
    ): ConversationEvent(), MedConnectEvent

}