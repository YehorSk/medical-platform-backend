package com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant

data class PatientAuditLogResponseDto(
    val id: AuditLogId,
    val action: AuditLogAction,
    val outcome: AuditLogOutcome,
    val detail: String,
    val createdAt: Instant,
    val actor: AuditLogActorDto
)

data class AuditLogActorDto(
    val id: UserId,
    val doctorId: DoctorId?,
    val name: String,
    val role: UserRole,
    @get:JsonProperty("isMe")
    val isMe: Boolean
)
