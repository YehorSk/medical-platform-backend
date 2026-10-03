package com.yehorsk.medicalplatformbackend.chat.websocket

import com.fasterxml.jackson.core.JacksonException
import com.yehorsk.medicalplatformbackend.chat.service.ConversationService
import com.yehorsk.medicalplatformbackend.chat.service.MessageService
import com.yehorsk.medicalplatformbackend.chat.service.dto.request.SendMessageRequestDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.ws.ErrorDto
import com.yehorsk.medicalplatformbackend.chat.service.dto.ws.IncomingWebSocketMessage
import com.yehorsk.medicalplatformbackend.chat.service.dto.ws.IncomingWebSocketMessageType
import com.yehorsk.medicalplatformbackend.chat.service.dto.ws.OutgoingWebSocketMessage
import com.yehorsk.medicalplatformbackend.chat.service.dto.ws.OutgoingWebSocketMessageType
import com.yehorsk.medicalplatformbackend.chat.service.exceptions.types.ConversationNotFoundException
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.util.JwtService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PingMessage
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatWebSocketHandler(
    private val messageService: MessageService,
    private val objectMapper: ObjectMapper,
    private val jwtService: JwtService
) : TextWebSocketHandler() {

    companion object {
        private const val PING_INTERVAL_MS = 30_000L
        private const val PONG_TIMEOUT_MS = 60_000L
        private const val MAX_CONTENT_LENGTH = 2000
        private const val SEND_TIME_LIMIT_MS = 5_000
        private const val SEND_BUFFER_LIMIT = 64 * 1024
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    private val sessions = ConcurrentHashMap<String, UserSession>()
    private val userToSessions = ConcurrentHashMap<UserId, MutableSet<String>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val authHeader = session.handshakeHeaders.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null) {
            logger.warn("Session {} closed: missing Authorization header", session.id)
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Authentication failed"))
            return
        }

        val userId = try {
            jwtService.getUserIdFromToken(authHeader)
        } catch (e: Exception) {
            logger.warn("Session {} closed: invalid token", session.id)
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Authentication failed"))
            return
        }

        val safeSession = ConcurrentWebSocketSessionDecorator(
            session, SEND_TIME_LIMIT_MS, SEND_BUFFER_LIMIT
        )

        sessions[session.id] = UserSession(userId = userId, session = safeSession)
        userToSessions.computeIfAbsent(userId) { ConcurrentHashMap.newKeySet() }.add(session.id)

        logger.info("WebSocket connected: user={}", userId)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val userSession = sessions.remove(session.id) ?: return
        userToSessions.computeIfPresent(userSession.userId) { _, ids ->
            ids.remove(session.id)
            ids.takeIf { it.isNotEmpty() }
        }
        logger.info("WebSocket closed: user={}", userSession.userId)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.warn("Transport error for session {}: {}", session.id, exception.message)
        runCatching { session.close(CloseStatus.SERVER_ERROR.withReason("Transport error")) }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val userSession = sessions[session.id] ?: return

        try {
            val incoming = objectMapper.readValue(message.payload, IncomingWebSocketMessage::class.java)
            when (incoming.type) {
                IncomingWebSocketMessageType.NEW_MESSAGE -> {
                    val request = objectMapper.readValue(incoming.payload, SendMessageRequestDto::class.java)
                    handleSendMessage(userSession, request)
                }
            }
        } catch (e: JacksonException) {
            logger.warn("Invalid JSON from user={}", userSession.userId)
            sendError(userSession.session, "INVALID_JSON", "Incoming JSON is invalid")
        } catch (e: ConversationNotFoundException) {
            logger.warn("Forbidden chat access attempt: user={}", userSession.userId)
            sendError(userSession.session, "FORBIDDEN", "Conversation not available")
        } catch (e: Exception) {
            logger.error("Failed to handle message from user={}", userSession.userId, e)
            sendError(userSession.session, "INTERNAL_ERROR", "Could not process message")
        }
    }

    private fun handleSendMessage(userSession: UserSession, request: SendMessageRequestDto) {
        val content = request.content.trim()
        if (content.isEmpty() || content.length > MAX_CONTENT_LENGTH) {
            sendError(userSession.session, "INVALID_CONTENT", "Message is empty or too long")
            return
        }

        val sent = messageService.sendMessage(
            senderId = userSession.userId,
            request = request.copy(content = content)
        )

        val json = objectMapper.writeValueAsString(
            OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.NEW_MESSAGE,
                payload = objectMapper.writeValueAsString(sent.message)
            )
        )

        sent.participantIds.forEach { userId ->
            userToSessions[userId]?.forEach { sessionId ->
                sessions[sessionId]?.session?.let { sendSafely(it, json) }
            }
        }
    }

    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
        sessions.computeIfPresent(session.id) { _, us ->
            us.copy(lastPongTimestamp = System.currentTimeMillis())
        }
    }

    @Scheduled(fixedDelay = PING_INTERVAL_MS)
    fun pingClients() {
        val now = System.currentTimeMillis()

        sessions.toMap().forEach { (sessionId, userSession) ->
            try {
                if (!userSession.session.isOpen) return@forEach

                if (now - userSession.lastPongTimestamp > PONG_TIMEOUT_MS) {
                    logger.warn("Session {} timed out", sessionId)
                    userSession.session.close(CloseStatus.GOING_AWAY.withReason("Ping timeout"))
                } else {
                    userSession.session.sendMessage(PingMessage())
                }
            } catch (e: Exception) {
                logger.warn("Ping failed for session {}: {}", sessionId, e.message)
                runCatching { userSession.session.close(CloseStatus.SERVER_ERROR) }
            }
        }
    }

    private fun sendError(session: WebSocketSession, code: String, message: String) {
        val json = objectMapper.writeValueAsString(
            OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.ERROR,
                payload = objectMapper.writeValueAsString(ErrorDto(code, message))
            )
        )
        sendSafely(session, json)
    }

    private fun sendSafely(session: WebSocketSession, json: String) {
        try {
            if (session.isOpen) session.sendMessage(TextMessage(json))
        } catch (e: Exception) {
            logger.warn("Could not send to session {}: {}", session.id, e.message)
        }
    }

    private data class UserSession(
        val userId: UserId,
        val session: WebSocketSession,
        val lastPongTimestamp: Long = System.currentTimeMillis()
    )
}