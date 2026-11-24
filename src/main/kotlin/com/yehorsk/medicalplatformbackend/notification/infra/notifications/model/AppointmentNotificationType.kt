package com.yehorsk.medicalplatformbackend.notification.infra.notifications.model

enum class AppointmentNotificationType {
    REQUESTED,
    RESCHEDULED,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    REMINDER
}
