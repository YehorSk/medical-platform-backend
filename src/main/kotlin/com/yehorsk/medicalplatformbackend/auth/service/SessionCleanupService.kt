package com.yehorsk.medicalplatformbackend.auth.service

import com.yehorsk.medicalplatformbackend.auth.database.repository.RefreshTokenRepository
import com.yehorsk.medicalplatformbackend.notification.service.PushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SessionCleanupService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val pushNotificationService: PushNotificationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(
        initialDelayString = "\${medical-platform.auth.session-cleanup-initial-delay:PT1M}",
        fixedDelayString = "\${medical-platform.auth.session-cleanup-interval:PT1H}"
    )
    @Transactional
    fun cleanUpExpiredSessions() {
        val now = Instant.now()

        val removedDevices = pushNotificationService.unregisterDevicesWithoutActiveSession(now)
        val removedRefreshTokens = refreshTokenRepository.deleteExpired(now)

        if (removedDevices > 0 || removedRefreshTokens > 0) {
            logger.info(
                "Session cleanup removed {} device tokens and {} expired refresh tokens",
                removedDevices, removedRefreshTokens
            )
        }
    }
}
