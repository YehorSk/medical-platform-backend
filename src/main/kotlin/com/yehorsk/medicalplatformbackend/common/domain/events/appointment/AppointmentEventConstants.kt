package com.yehorsk.medicalplatformbackend.common.domain.events.appointment

object AppointmentEventConstants {
    const val APPOINTMENT_EXCHANGE = "appointment.events"

    const val APPOINTMENT_REQUESTED = "appointment.requested"
    const val APPOINTMENT_RESCHEDULED = "appointment.rescheduled"
    const val APPOINTMENT_CONFIRMED = "appointment.confirmed"
    const val APPOINTMENT_REJECTED = "appointment.rejected"
    const val APPOINTMENT_CANCELLED = "appointment.cancelled"
    const val APPOINTMENT_REMINDER = "appointment.reminder"
}
