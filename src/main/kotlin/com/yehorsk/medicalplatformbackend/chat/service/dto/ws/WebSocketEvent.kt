package com.yehorsk.medicalplatformbackend.chat.service.dto.ws

enum class IncomingWebSocketMessageType {
    NEW_MESSAGE
}

enum class OutgoingWebSocketMessageType {
    NEW_MESSAGE,
    ACCESS_REVOKED,
    ERROR
}

data class IncomingWebSocketMessage(
    val type: IncomingWebSocketMessageType,
    val payload: String
)

data class OutgoingWebSocketMessage(
    val type: OutgoingWebSocketMessageType,
    val payload: String
)