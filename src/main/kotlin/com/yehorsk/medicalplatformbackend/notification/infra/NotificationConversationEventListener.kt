package com.yehorsk.medicalplatformbackend.notification.infra

import com.yehorsk.medicalplatformbackend.common.domain.events.conversation.ConversationEvent
import com.yehorsk.medicalplatformbackend.common.infra.MessageQueues
import com.yehorsk.medicalplatformbackend.notification.service.PushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NotificationConversationEventListener(
    private val pushNotificationService: PushNotificationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_CONVERSATION_EVENTS])
    fun handleConversationEvent(event: ConversationEvent) {
        try {
            when(event){
                is ConversationEvent.NewMessage -> {
                    pushNotificationService.sendNewMessageNotifications(
                        recipientUserIds = listOf(event.recipient),
                        senderUserId = event.senderId,
                        senderUsername = event.senderUsername,
                        conversationId = event.conversationId
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to send push notification for event {}", event.eventId, e)
        }
    }

}
