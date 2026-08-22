package com.yehorsk.medicalplatformbackend.chat.service.mappers

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.chat.database.entity.ConversationEntity
import com.yehorsk.medicalplatformbackend.chat.database.entity.MessageEntity
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.ConversationResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.MessageResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.ParticipantDto

fun ConversationEntity.toConversationResponseDto(lastMessage: MessageEntity?) = ConversationResponseDto(
    id = this.id!!,
    patient = this.patient.toParticipantDto(),
    doctor = this.doctor.toParticipantDto(),
    lastMessageAt = this.lastMessageAt,
    lastMessage = lastMessage?.toMessageResponseDto()
)

fun UserEntity.toParticipantDto() = ParticipantDto(
    userId = this.id!!,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    title = this.title
)

fun MessageEntity.toMessageResponseDto() = MessageResponseDto(
    id = this.id!!,
    conversationId = this.conversationId,
    senderId = this.sender!!.id!!,
    content = this.content,
    createdAt = this.createdAt
)