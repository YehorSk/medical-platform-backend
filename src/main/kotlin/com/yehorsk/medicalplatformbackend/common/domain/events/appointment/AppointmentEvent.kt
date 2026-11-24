package com.yehorsk.medicalplatformbackend.common.domain.events.appointment

import com.yehorsk.medicalplatformbackend.common.domain.events.MedConnectEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant
import java.util.UUID

sealed class AppointmentEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = AppointmentEventConstants.APPOINTMENT_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
) : MedConnectEvent {

    abstract val appointmentId: AppointmentId
    abstract val dateTime: Instant

    data class Requested(
        override val appointmentId: AppointmentId,
        override val dateTime: Instant,
        val doctorId: UserId,
        val patientName: String,
        override val eventKey: String = AppointmentEventConstants.APPOINTMENT_REQUESTED
    ) : AppointmentEvent(), MedConnectEvent

    data class Rescheduled(
        override val appointmentId: AppointmentId,
        override val dateTime: Instant,
        val doctorId: UserId,
        val patientName: String,
        override val eventKey: String = AppointmentEventConstants.APPOINTMENT_RESCHEDULED
    ) : AppointmentEvent(), MedConnectEvent

    data class Confirmed(
        override val appointmentId: AppointmentId,
        override val dateTime: Instant,
        val patientId: UserId,
        val doctorName: String,
        override val eventKey: String = AppointmentEventConstants.APPOINTMENT_CONFIRMED
    ) : AppointmentEvent(), MedConnectEvent

    data class Rejected(
        override val appointmentId: AppointmentId,
        override val dateTime: Instant,
        val patientId: UserId,
        val doctorName: String,
        override val eventKey: String = AppointmentEventConstants.APPOINTMENT_REJECTED
    ) : AppointmentEvent(), MedConnectEvent

    // Sent to the participant who did not cancel
    data class Cancelled(
        override val appointmentId: AppointmentId,
        override val dateTime: Instant,
        val recipientId: UserId,
        val cancelledByName: String,
        override val eventKey: String = AppointmentEventConstants.APPOINTMENT_CANCELLED
    ) : AppointmentEvent(), MedConnectEvent

    data class Reminder(
        override val appointmentId: AppointmentId,
        override val dateTime: Instant,
        val patientId: UserId,
        val doctorName: String,
        override val eventKey: String = AppointmentEventConstants.APPOINTMENT_REMINDER
    ) : AppointmentEvent(), MedConnectEvent
}
