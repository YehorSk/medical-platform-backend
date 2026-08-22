package com.yehorsk.medicalplatformbackend.chat.service.exceptions

import com.yehorsk.medicalplatformbackend.chat.service.exceptions.types.ConversationNotFoundException
import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ConversationExceptionHandler {

    @ExceptionHandler(ConversationNotFoundException::class)
    fun onConversationNotFound(e: ConversationNotFoundException, request: HttpServletRequest) =
        e.toResponse(request)

}