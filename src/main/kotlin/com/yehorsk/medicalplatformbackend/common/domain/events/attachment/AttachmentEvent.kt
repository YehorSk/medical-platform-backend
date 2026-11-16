package com.yehorsk.medicalplatformbackend.common.domain.events.attachment

import com.yehorsk.medicalplatformbackend.common.domain.events.MedConnectEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant
import java.util.UUID

sealed class AttachmentEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = AttachmentEventConstants.ATTACHMENT_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
) : MedConnectEvent {

    data class Uploaded(
        val attachmentId: MedicalRecordAttachmentId,
        override val eventKey: String = AttachmentEventConstants.ATTACHMENT_UPLOADED
    ) : AttachmentEvent(), MedConnectEvent

    // The scan ended without a usable file (INFECTED or SCAN_FAILED); the uploading doctor should re-upload it
    data class ScanFailed(
        val attachmentId: MedicalRecordAttachmentId,
        val medicalRecordId: MedicalRecordId,
        val doctorId: UserId,
        val status: String,
        override val eventKey: String = AttachmentEventConstants.ATTACHMENT_SCAN_FAILED
    ) : AttachmentEvent(), MedConnectEvent
}
