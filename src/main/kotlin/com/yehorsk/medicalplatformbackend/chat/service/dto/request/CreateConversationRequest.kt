package com.yehorsk.medicalplatformbackend.chat.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import org.jetbrains.annotations.NotNull

data class CreateConversationRequest(
    @field:NotNull
    val patientId: UserId,
    @field:NotNull
    val doctorId: UserId
)
