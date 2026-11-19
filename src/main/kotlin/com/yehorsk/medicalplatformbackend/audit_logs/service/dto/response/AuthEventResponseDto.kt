package com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditFailureReason
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import java.time.Instant

data class AuthEventResponseDto(
    val id: AuditLogId,
    /** Only account actions: LOGIN, LOGOUT, PASSWORD_RESET_REQUESTED, PASSWORD_RESET_CODE_CHECK, PASSWORD_RESET */
    val type: AuditLogAction,
    val outcome: AuditLogOutcome,
    val failureReason: AuditFailureReason?,
    val ipAddress: String?,
    val userAgent: String?,
    val createdAt: Instant
)
