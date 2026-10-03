package com.yehorsk.medicalplatformbackend.chat.service

import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.chat.database.entity.MessageEntity
import com.yehorsk.medicalplatformbackend.chat.database.repository.ConversationRepository
import com.yehorsk.medicalplatformbackend.chat.database.repository.MessageRepository
import com.yehorsk.medicalplatformbackend.chat.service.dto.request.SendMessageRequestDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.MessageResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.exceptions.types.ConversationNotFoundException
import com.yehorsk.medicalplatformbackend.chat.service.mappers.toMessageResponseDto
import com.yehorsk.medicalplatformbackend.common.domain.events.conversation.ConversationEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    @CacheEvict(value = ["messages"], key = "#request.conversationId")
    fun sendMessage(senderId: UserId, request: SendMessageRequestDto): SentMessage {
        val conversation = conversationRepository
            .findConversationEntityByParticipantId(request.conversationId, senderId)
            ?: throw ConversationNotFoundException()

        val sender = userRepository.findUserEntityById(senderId)
            ?: throw ConversationNotFoundException()

        val message = messageRepository.saveAndFlush(
            MessageEntity(
                conversationId = request.conversationId,
                conversation = conversation,
                content = request.content,
                sender = sender
            )
        )

        conversation.lastMessageAt = message.createdAt

        val doctorId = conversation.doctor.id!!
        val patientId = conversation.patient.id!!
        val recipientId = if (senderId == doctorId) patientId else doctorId

        eventPublisher.publish(
            ConversationEvent.NewMessage(
                senderId = senderId,
                recipient = recipientId,
                senderUsername = "${sender.firstName} ${sender.lastName}",
                conversationId = request.conversationId,
                content = request.content
            )
        )

        return SentMessage(message.toMessageResponseDto(), listOf(doctorId, patientId))
    }
}

data class SentMessage(
    val message: MessageResponseDto,
    val participantIds: List<UserId>
)