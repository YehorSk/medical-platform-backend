package com.yehorsk.medicalplatformbackend.common.domain.events

import java.time.Instant

interface MedConnectEvent {
    val eventId: String
    val eventKey: String
    val occurredAt: Instant
    val exchange: String
}