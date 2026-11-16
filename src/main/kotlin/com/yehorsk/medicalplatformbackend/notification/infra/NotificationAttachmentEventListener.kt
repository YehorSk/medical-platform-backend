package com.yehorsk.medicalplatformbackend.notification.infra

import com.yehorsk.medicalplatformbackend.common.domain.events.attachment.AttachmentEvent
import com.yehorsk.medicalplatformbackend.common.infra.MessageQueues
import com.yehorsk.medicalplatformbackend.notification.service.PushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NotificationAttachmentEventListener(
    private val pushNotificationService: PushNotificationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_ATTACHMENT_EVENTS])
    fun handleAttachmentEvent(event: AttachmentEvent) {
        // Never rethrow: RabbitMQ would requeue the event and resend the push in an endless loop.
        // Failed devices are already retried by PushNotificationService.
        try {
            when (event) {
                is AttachmentEvent.ScanFailed -> {
                    pushNotificationService.sendAttachmentScanFailedNotification(
                        doctorId = event.doctorId,
                        medicalRecordId = event.medicalRecordId,
                        attachmentId = event.attachmentId,
                        status = event.status
                    )
                }
                // Not routed to this queue; the scan worker handles it
                is AttachmentEvent.Uploaded -> Unit
            }
        } catch (e: Exception) {
            logger.error("Failed to send push notification for event {}", event.eventId, e)
        }
    }
}
