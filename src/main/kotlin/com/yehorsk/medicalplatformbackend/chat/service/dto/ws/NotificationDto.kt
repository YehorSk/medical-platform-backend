package com.yehorsk.medicalplatformbackend.chat.service.dto.ws

data class NotificationDto(
    val title: String,
    val body: String,
    val data: Map<String, String>
)
