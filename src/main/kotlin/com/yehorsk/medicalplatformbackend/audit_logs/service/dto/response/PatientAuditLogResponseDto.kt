package com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant

/** One entry of "who accessed my medical card", as shown to the patient */
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
    /** Full name with title, e.g. "MUDr. Jan Novak" */
    val name: String,
    val role: UserRole,
    /** True when the patient did this themself. Annotated so JSON keeps the name "isMe", not "me" */
    @get:JsonProperty("isMe")
    val isMe: Boolean
)
