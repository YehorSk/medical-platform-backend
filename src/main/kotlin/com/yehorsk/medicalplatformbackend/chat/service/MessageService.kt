package com.yehorsk.medicalplatformbackend.chat.service

import com.yehorsk.medicalplatformbackend.chat.database.entity.MessageEntity
import com.yehorsk.medicalplatformbackend.chat.database.repository.ConversationRepository
import com.yehorsk.medicalplatformbackend.chat.database.repository.MessageRepository
import com.yehorsk.medicalplatformbackend.chat.service.dto.request.SendMessageRequestDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.MessageResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.exceptions.types.ConversationNotFoundException
import com.yehorsk.medicalplatformbackend.chat.service.mappers.toMessageResponseDto
import com.yehorsk.medicalplatformbackend.common.domain.events.conversation.ConversationEvent
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import org.springframework.cache.annotation.CacheEvict
import org.springframework.transaction.annotation.Transactional

open class MessageService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    @CacheEvict(
        value = ["messages"],
        key = "#chatId",
    )
    open fun sendMessage(
        request: SendMessageRequestDto
    ): MessageResponseDto{
        val conversation = conversationRepository.findConversationEntityByParticipantId(request.conversationId, currentUserProvider.getCurrentUserId())
            ?: throw ConversationNotFoundException()
        val sender = currentUserProvider.getCurrentUserEntity()

        val message = messageRepository.saveAndFlush(
            MessageEntity(
                conversationId = request.conversationId,
                conversation = conversation,
                content = request.content,
                sender = sender
            )
        )

        eventPublisher.publish(
            event = ConversationEvent.NewMessage(
                senderId = sender.id!!,
                recipient = conversation.doctor.id!!,
                senderUsername = "${sender.firstName} ${sender.lastName}",
                conversationId = request.conversationId,
                content = request.content
            )
        )

        return message.toMessageResponseDto()
    }

}