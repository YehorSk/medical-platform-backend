package com.yehorsk.medicalplatformbackend.medical_records.infra

import com.yehorsk.medicalplatformbackend.common.domain.events.attachment.AttachmentEvent
import com.yehorsk.medicalplatformbackend.common.infra.MessageQueues
import com.yehorsk.medicalplatformbackend.medical_records.service.AttachmentScanService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class AttachmentScanEventListener(
    private val attachmentScanService: AttachmentScanService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.ATTACHMENT_SCAN])
    fun handleAttachmentEvent(event: AttachmentEvent) {
        // Never rethrow: RabbitMQ would requeue immediately in a tight loop.
        // AttachmentScanService retries with backoff itself, and its sweeper re-queues anything left behind.
        try {
            when (event) {
                is AttachmentEvent.Uploaded -> attachmentScanService.scan(event.attachmentId)
                // Not routed to this queue; handled by the notification module
                is AttachmentEvent.ScanFailed -> Unit
            }
        } catch (e: Exception) {
            logger.error("Failed to handle attachment event {}", event.eventId, e)
        }
    }
}
