package com.yehorsk.medicalplatformbackend.chat.service.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException

import org.springframework.http.HttpStatus

class ConversationNotFoundException: AppException("CONVERSATION_NOT_FOUND", HttpStatus.NOT_FOUND, "Conversation not found")

class ConversationNotActiveException: AppException("CONVERSATION_NOT_ACTIVE", HttpStatus.FORBIDDEN, "Doctor access is not approved for this conversation")