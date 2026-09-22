package com.yehorsk.medicalplatformbackend.common.domain.events.audit

import com.yehorsk.medicalplatformbackend.common.domain.events.MedConnectEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant
import java.util.UUID

sealed class AuditLogEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = AuditLogEventConstants.AUDIT_LOG_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
) : MedConnectEvent {

    data class MedicalCardAccessed(
        val patientId: UserId,
        val patientEmail: String,
        val doctorId: UserId,
        val doctorUsername: String,
        val patientUsername: String,
        override val eventKey: String = AuditLogEventConstants.AUDIT_LOG_MEDICAL_CARD_ACCESSED
    ) : AuditLogEvent(), MedConnectEvent
}
