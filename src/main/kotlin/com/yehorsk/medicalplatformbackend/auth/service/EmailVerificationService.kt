package com.yehorsk.medicalplatformbackend.auth.service

import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidTokenException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.TooManyRequestsException
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
    private val userRepository: UserRepository
) {

    private val secureRandom = SecureRandom()

    companion object {
        private const val MAX_RESEND_PER_HOUR = 5L
    }

    @Transactional
    fun resendEmailVerification(email: String) {

        val user = userRepository.findByEmail(email)

        if (user != null && !user.hasVerifiedEmail) {
            val token = generateSecureToken()

            val rateLimitKey = "email_verify:resend_limit:$email"

            val attempts = redisTemplate.opsForValue().increment(rateLimitKey) ?: 1
            if (attempts == 1L) {
                redisTemplate.expire(rateLimitKey, Duration.ofHours(1))
            }

            if (attempts > MAX_RESEND_PER_HOUR) {
                throw TooManyRequestsException()
            }

            redisTemplate.opsForValue().set("verification_token:$token", user.id.toString(), Duration.ofHours(24))

            val verificationLink = UriComponentsBuilder
                .newInstance()
                .scheme("medicalplatform")
                .host("app")
                .path("/verify-email")
                .queryParam("token", token)
                .build()
                .toUriString()

            mailService.sendPlainText(
                to = email,
                subject = "Verify your email",
                body = "Click here: $verificationLink"
            )
        }
    }

    @Transactional
    fun sendEmailVerification(email: String): ApiResponse {
        val user = userRepository.findByEmail(email)
            ?: throw InvalidCredentialsException()

        val otp = generateSecureToken()
        redisTemplate.opsForValue().set("verification_token:$otp", user.id.toString(), Duration.ofHours(24))

        val verificationLink = UriComponentsBuilder
            .newInstance()
            .scheme("medicalplatform")
            .host("app")
            .path("/verify-email")
            .queryParam("token", otp)
            .build()
            .toUriString()

        mailService.sendPlainText(to = email, subject = "Verify your email", body = "Click here: $verificationLink")
        return ApiResponse(message = "Verification token sent successfully")
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