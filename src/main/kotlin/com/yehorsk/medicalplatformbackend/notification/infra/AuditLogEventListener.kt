package com.yehorsk.medicalplatformbackend.notification.infra

import com.yehorsk.medicalplatformbackend.common.domain.events.audit.AuditLogEvent
import com.yehorsk.medicalplatformbackend.common.infra.MessageQueues
import com.yehorsk.medicalplatformbackend.common.service.MailService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class AuditLogEventListener(
    private val mailService: MailService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_AUDIT_LOG_EVENTS])
    fun handle(event: AuditLogEvent) {
        when (event) {
            is AuditLogEvent.MedicalCardAccessed -> {
                mailService.sendMedicalCardAccessedEmail(
                    email = event.patientEmail,
                    username = event.patientUsername,
                    doctorUsername = event.doctorUsername
                )
            }
        }
    }
}

