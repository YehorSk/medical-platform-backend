package com.yehorsk.medicalplatformbackend.chat.service
import com.yehorsk.medicalplatformbackend.chat.database.entity.ConversationEntity
import com.yehorsk.medicalplatformbackend.chat.database.repository.ConversationRepository
import com.yehorsk.medicalplatformbackend.chat.database.repository.MessageRepository
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.ConversationResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.MessageResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.exceptions.types.ConversationNotFoundException
import com.yehorsk.medicalplatformbackend.chat.service.mappers.toMessageResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.mappers.toParticipantDto
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    fun getConversationMessages(
        conversationId: ConversationId,
        pageSize: Int
    ): List<MessageResponseDto>{
        return messageRepository
            .findByConversationIdOrderByCreatedAtDesc(
                conversationId,
                pageable = PageRequest.of(0, pageSize)
            )
            .content
            .asReversed()
            .map {
                it.toMessageResponseDto()
            }
    }

    fun getConversationById(
        conversationId: ConversationId
    ): ConversationEntity {
        val userId = currentUserProvider.getCurrentUserId()
        val conversation = conversationRepository.findConversationEntityByParticipantId(
            conversationId = conversationId,
            userId = userId
        ) ?: throw ConversationNotFoundException()

        return conversation
    }

    fun findConversationByUser(): List<ConversationResponseDto> {
        val userId = currentUserProvider.getCurrentUserId()
        val conversations = conversationRepository.findAllByParticipantId(userId)
        val conversationIds = conversations.mapNotNull { it.id }
        val latestMessages = messageRepository
            .findLatestMessagesByConversationIds(conversationIds.toSet())
            .associateBy { it.conversationId }

        return conversations.map {
            ConversationResponseDto(
                id = it.id!!,
                patient = it.patient.toParticipantDto(),
                doctor = it.doctor.toParticipantDto(),
                lastMessageAt = it.lastMessageAt,
                lastMessage = latestMessages[it.id]?.toMessageResponseDto()
            )
        }
    }

}