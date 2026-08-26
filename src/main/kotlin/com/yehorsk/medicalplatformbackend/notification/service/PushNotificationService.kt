package com.yehorsk.medicalplatformbackend.notification.service

import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.notification.infra.database.DeviceTokenRepository
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.DeviceTokenEntity
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.PlatformEntity
import com.yehorsk.medicalplatformbackend.notification.infra.mappers.toDeviceToken
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.DeviceToken
import com.yehorsk.medicalplatformbackend.notification.infra.notifications.model.PushNotification
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentSkipListMap

@Service
class PushNotificationService(
    private val tokenRepository: DeviceTokenRepository,
    private val firebaseService: FirebaseNotificationService
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
        val storedToken = tokenRepository.findByToken(token)

        if (storedToken == null &&
            !firebaseService.isValidToken(normalizedToken)
        ) {
            // throw InvalidDeviceTokenException()
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
    fun unregisterDevice(token: String) {
        tokenRepository.deleteByToken(token.trim())
    }

    fun sendNewMessageNotifications(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        message: String,
        conversationId: ConversationId
    ) {
        val tokens = tokenRepository.findByUserIdIn(recipientUserIds)

        if (tokens.isEmpty()) {
            log.info("No device tokens found for $recipientUserIds")
            return
        }

        val recipients = tokens
            .asSequence()
            .filterNot { it.userId == senderUserId }
            .map(DeviceTokenEntity::toDeviceToken)
            .toList()

        val push = PushNotification(
            title = "New message from $senderUsername",
            recipients = recipients,
            message = message,
            conversationId = conversationId,
            data = buildMap {
                put("conversationId", conversationId.toString())
                put("type", "new_message")
            }
        )

        sendWithRetry(push)
    }

    fun sendWithRetry(
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