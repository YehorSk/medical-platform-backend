package com.yehorsk.medicalplatformbackend.chat.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId

data class ParticipantDto(
    val userId: UserId,
    val firstName: String,
    val lastName: String,
    val email: String,
    val title: String? = null
)
