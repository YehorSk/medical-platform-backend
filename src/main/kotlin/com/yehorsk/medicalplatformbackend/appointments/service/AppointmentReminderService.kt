package com.yehorsk.medicalplatformbackend.appointments.service

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.common.domain.events.appointment.AppointmentEvent
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

@Service
class AppointmentReminderService(
    private val appointmentRepository: AppointmentRepository,
    private val eventPublisher: EventPublisher,
    @param:Value("\${medical-platform.appointments.reminder-lead-time:PT24H}")
    private val reminderLeadTime: Duration
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${medical-platform.appointments.reminder-check-interval:PT1M}")
    @Transactional
    fun sendDueReminders() {
        val now = Instant.now()
        val due = appointmentRepository.findDueForReminder(
            status = AppointmentStatus.CONFIRMED,
            from = now,
            to = now.plus(reminderLeadTime)
        )

        val claimed = due.filter { appointmentRepository.markReminderSent(it.id!!, now) > 0 }

        claimed.forEach { appointment ->
            eventPublisher.publishAfterCommit(
                AppointmentEvent.Reminder(
                    appointmentId = appointment.id!!,
                    dateTime = appointment.dateTime,
                    patientId = appointment.patient.id!!,
                    doctorName = "${appointment.doctor.firstName} ${appointment.doctor.lastName}"
                )
            )
        }

        if (claimed.isNotEmpty()) {
            logger.info("Queued {} appointment reminders", claimed.size)
        }
    }
}
