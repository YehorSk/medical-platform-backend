package com.yehorsk.medicalplatformbackend.notification.service

import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.notification.exceptions.types.InvalidDeviceTokenException
import com.yehorsk.medicalplatformbackend.notification.infra.database.DeviceTokenRepository
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.DeviceTokenEntity
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.PlatformEntity
import com.yehorsk.medicalplatformbackend.notification.infra.mappers.toDeviceToken
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.AppointmentNotificationType
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.DeviceToken
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.PushNotification
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.UserNotificationEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentSkipListMap

@Service
class PushNotificationService(
    private val tokenRepository: DeviceTokenRepository,
    private val firebaseService: FirebaseNotificationService,
    private val eventPublisher: ApplicationEventPublisher
) {

    companion object {
        private val RETRY_INTERVALS = longArrayOf(30, 60, 120, 300, 600)
        private const val RETRY_EXPIRATION_MINUTES = 30L
    }

    private val pendingRetries =
        ConcurrentSkipListMap<Long, MutableList<RetryData>>()

    private val log = LoggerFactory.getLogger(PushNotificationService::class.java)

    @Transactional
    fun registerDevice(
        userId: UserId,
        token: String,
        platform: PlatformEntity
    ): DeviceToken {
        val normalizedToken = token.trim()
        val storedToken = tokenRepository.findByToken(normalizedToken)

        if (storedToken == null &&
            !firebaseService.isValidToken(normalizedToken)
        ) {
            throw InvalidDeviceTokenException()
        }

        val saved = storedToken?.let { existing ->
            existing.userId = userId
            tokenRepository.save(existing)
        } ?: tokenRepository.save(
            DeviceTokenEntity(
                userId = userId,
                token = normalizedToken,
                platform = platform
            )
        )

        return saved.toDeviceToken()
    }

    @Transactional
    fun unregisterDevice(userId: UserId, token: String) {
        tokenRepository.deleteByTokenAndUserId(token.trim(), userId)
    }

    @Transactional
    fun unregisterAllDevices(userId: UserId) {
        tokenRepository.deleteByUserId(userId)
    }

    // A session that expired without logout leaves its devices behind; they must stop receiving pushes
    @Transactional
    fun unregisterDevicesWithoutActiveSession(now: Instant): Int =
        tokenRepository.deleteForUsersWithoutActiveSession(now)

    fun sendNewMessageNotifications(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        conversationId: ConversationId
    ) {
        notifyUsers(
            userIds = recipientUserIds - senderUserId,
            title = "New message from $senderUsername",
            message = "You have a new message",
            collapseKey = conversationId.toString(),
            data = mapOf(
                "type" to "new_message",
                "conversationId" to conversationId.toString()
            )
        )
    }

    fun sendAttachmentScanFailedNotification(
        doctorId: UserId,
        medicalRecordId: MedicalRecordId,
        attachmentId: MedicalRecordAttachmentId,
        status: String
    ) {
        notifyUsers(
            userIds = listOf(doctorId),
            title = "File upload problem",
            message = "A file attached to a medical record could not be verified. Please upload it again.",
            collapseKey = "medical-record-$medicalRecordId",
            data = mapOf(
                "type" to "attachment_scan_failed",
                "medicalRecordId" to medicalRecordId.toString(),
                "attachmentId" to attachmentId.toString(),
                "status" to status
            )
        )
    }

    fun sendAppointmentNotification(
        recipientId: UserId,
        appointmentId: AppointmentId,
        dateTime: Instant,
        type: AppointmentNotificationType,
        counterpartName: String
    ) {
        val (title, message) = when (type) {
            AppointmentNotificationType.REQUESTED ->
                "New appointment request" to "$counterpartName requested an appointment"
            AppointmentNotificationType.RESCHEDULED ->
                "Appointment rescheduled" to "$counterpartName moved their appointment to a new time"
            AppointmentNotificationType.CONFIRMED ->
                "Appointment confirmed" to "$counterpartName confirmed your appointment"
            AppointmentNotificationType.REJECTED ->
                "Appointment declined" to "$counterpartName declined your appointment request"
            AppointmentNotificationType.CANCELLED ->
                "Appointment cancelled" to "$counterpartName cancelled the appointment"
            AppointmentNotificationType.REMINDER ->
                "Upcoming appointment" to "You have an appointment with $counterpartName soon"
        }

        notifyUsers(
            userIds = listOf(recipientId),
            title = title,
            message = message,
            collapseKey = "appointment-$appointmentId",
            data = mapOf(
                "type" to "appointment_${type.name.lowercase()}",
                "appointmentId" to appointmentId.toString(),
                "dateTime" to dateTime.toString()
            )
        )
    }

    private fun notifyUsers(
        userIds: Collection<UserId>,
        title: String,
        message: String,
        collapseKey: String,
        data: Map<String, String>
    ) {
        if (userIds.isEmpty()) return

        // Before the token lookup: desktop clients have no FCM token and rely on this alone
        eventPublisher.publishEvent(
            UserNotificationEvent(userIds = userIds, title = title, body = message, data = data)
        )

        val recipients = tokenRepository.findByUserIdIn(userIds.distinct())
            .map(DeviceTokenEntity::toDeviceToken)

        if (recipients.isEmpty()) {
            log.debug("No device tokens found for users {}", userIds)
            return
        }

        sendWithRetry(
            PushNotification(
                title = title,
                recipients = recipients,
                message = message,
                collapseKey = collapseKey,
                data = data
            )
        )
    }

    private fun sendWithRetry(
        notification: PushNotification,
        attempt: Int = 0
    ) {
        val response = firebaseService.sendNotification(notification)

        response.permanentFailures
            .map { it.token }
            .forEach(tokenRepository::deleteByToken)

        val failures = response.temporaryFailures

        if (failures.isNotEmpty() && attempt < RETRY_INTERVALS.size) {
            enqueue(
                notification.copy(recipients = failures),
                attempt + 1
            )
        }

        response.succeeded
            .takeIf { it.isNotEmpty() }
            ?.let {
                log.info(
                    "Successfully sent notification to ${it.size} devices"
                )
            }
    }

    private fun enqueue(
        notification: PushNotification,
        attempt: Int
    ) {
        val retryIndex = (attempt - 1).coerceIn(0, RETRY_INTERVALS.lastIndex)
        val delaySeconds = RETRY_INTERVALS[retryIndex]

        val scheduledAt = Instant.now()
            .plusSeconds(delaySeconds)
            .toEpochMilli()

        val retry = RetryData(
            notification = notification,
            attempt = attempt,
            createdAt = Instant.now()
        )

        pendingRetries.merge(
            scheduledAt,
            mutableListOf(retry)
        ) { current, incoming ->
            current.apply { addAll(incoming) }
        }

        log.info(
            "Scheduled retry $attempt for ${notification.id} in $delaySeconds seconds"
        )
    }

    @Scheduled(fixedDelay = 15_000L)
    fun processRetries() {
        val currentTime = Instant.now()
        val dueRetries = pendingRetries
            .headMap(currentTime.toEpochMilli(), true)
            .entries
            .toList()

        if (dueRetries.isEmpty()) {
            return
        }

        dueRetries.forEach { (timestamp, retries) ->
            pendingRetries.remove(timestamp)

            retries.forEach { retry ->
                try {
                    val elapsed = Duration.between(
                        retry.createdAt,
                        currentTime
                    )

                    if (elapsed.toMinutes() > RETRY_EXPIRATION_MINUTES) {
                        log.warn(
                            "Dropping old retry (${elapsed.toMinutes()} old)"
                        )
                    } else {
                        sendWithRetry(
                            retry.notification,
                            retry.attempt
                        )
                    }
                } catch (exception: Exception) {
                    log.warn(
                        "Error processing retry ${retry.notification.id}",
                        exception
                    )
                }
            }
        }
    }

    private data class RetryData(
        val notification: PushNotification,
        val attempt: Int,
        val createdAt: Instant
    )
}