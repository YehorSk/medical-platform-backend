package com.yehorsk.medicalplatformbackend.auth.service

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidTokenException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.TooManyRequestsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.common.domain.events.user.UserEvent
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.service.MailService
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.security.SecureRandom
import java.time.Duration
import java.util.Base64
import java.util.UUID

@Service
class EmailVerificationService(
    private val redisTemplate: StringRedisTemplate,
    private val mailService: MailService,
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) {

    private val secureRandom = SecureRandom()

    companion object {
        private const val MAX_RESEND_PER_HOUR = 5L
    }

    fun generateVerificationToken(email: String): Pair<String, UserEntity> {
        val user = userRepository.findByEmail(email)
            ?: throw UserDoesNotExistException()

        val token = generateSecureToken()

        redisTemplate.opsForValue().set("verification_token:$token", user.id.toString(), Duration.ofHours(24))
        return Pair(token, user)
    }

    @Transactional
    fun resendEmailVerification(email: String) {
        val (token, user) = generateVerificationToken(email)

        if(user.hasVerifiedEmail) {
            return
        }

        eventPublisher.publish(
            event = UserEvent.RequestResendVerification(
                userId = user.id!!,
                email = user.email,
                username = "${user.firstName} ${user.lastName}",
                verificationToken = token
            )
        )
    }

    @Transactional
    fun verifyEmail(token: String): ApiResponse{
        val tokenKey = "verification_token:$token"

        val userId = redisTemplate.opsForValue().get(tokenKey)
            ?: throw InvalidTokenException()

        val user = userRepository.findUserEntityById(UUID.fromString(userId))
            ?: throw InvalidCredentialsException()

        if(user.hasVerifiedEmail) {
            return ApiResponse(message = "User is already verified")
        }

        user.hasVerifiedEmail = true
        userRepository.save(user)
        redisTemplate.delete(tokenKey)

        return ApiResponse(message = "Email verified successfully")
    }

    fun generateSecureToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

}