package com.yehorsk.medicalplatformbackend.chat.contoller

import com.yehorsk.medicalplatformbackend.chat.service.ConversationService
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.ConversationResponseDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.response.MessageResponseDto
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/conversations")
class ConversationController(
    private val conversationService: ConversationService
) {

    @GetMapping
    fun getAllConversations(): ApiResponseWithData<List<ConversationResponseDto>> {
        val conversations = conversationService.findConversationByUser()
        return ApiResponseWithData(data = conversations)
    }

    @GetMapping("/{conversationId}")
    fun getConversationById(
        @PathVariable conversationId: ConversationId
    ): ApiResponseWithData<Any> {
        val conversation = conversationService.getConversationById(conversationId)
        return ApiResponseWithData(data = conversation)
    }

    @GetMapping("/{conversationId}/messages")
    fun getConversationMessages(
        @PathVariable conversationId: ConversationId,
        @RequestParam(defaultValue = "50") pageSize: Int
    ): ApiResponseWithData<List<MessageResponseDto>> {
        val messages = conversationService.getConversationMessages(conversationId, pageSize)
        return ApiResponseWithData(data = messages)
    }
}

