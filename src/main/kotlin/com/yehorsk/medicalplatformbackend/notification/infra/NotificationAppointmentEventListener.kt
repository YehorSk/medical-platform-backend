package com.yehorsk.medicalplatformbackend.notification.infra

import com.yehorsk.medicalplatformbackend.common.domain.events.appointment.AppointmentEvent
import com.yehorsk.medicalplatformbackend.common.infra.MessageQueues
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.AppointmentNotificationType
import com.yehorsk.medicalplatformbackend.notification.service.PushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NotificationAppointmentEventListener(
    private val pushNotificationService: PushNotificationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_APPOINTMENT_EVENTS])
    fun handleAppointmentEvent(event: AppointmentEvent) {
        try {
            val (recipientId, type, counterpartName) = when (event) {
                is AppointmentEvent.Requested ->
                    Triple(event.doctorId, AppointmentNotificationType.REQUESTED, event.patientName)
                is AppointmentEvent.Rescheduled ->
                    Triple(event.doctorId, AppointmentNotificationType.RESCHEDULED, event.patientName)
                is AppointmentEvent.Confirmed ->
                    Triple(event.patientId, AppointmentNotificationType.CONFIRMED, event.doctorName)
                is AppointmentEvent.Rejected ->
                    Triple(event.patientId, AppointmentNotificationType.REJECTED, event.doctorName)
                is AppointmentEvent.Cancelled ->
                    Triple(event.recipientId, AppointmentNotificationType.CANCELLED, event.cancelledByName)
                is AppointmentEvent.Reminder ->
                    Triple(event.patientId, AppointmentNotificationType.REMINDER, event.doctorName)
            }

            pushNotificationService.sendAppointmentNotification(
                recipientId = recipientId,
                appointmentId = event.appointmentId,
                dateTime = event.dateTime,
                type = type,
                counterpartName = counterpartName
            )
        } catch (e: Exception) {
            logger.error("Failed to send push notification for event {}", event.eventId, e)
        }
    }
}
