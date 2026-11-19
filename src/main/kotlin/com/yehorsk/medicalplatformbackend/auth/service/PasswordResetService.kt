package com.yehorsk.medicalplatformbackend.auth.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditFailureReason
import com.yehorsk.medicalplatformbackend.audit_logs.service.AuthEventService
import com.yehorsk.medicalplatformbackend.common.service.MailService
import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.auth.database.repository.RefreshTokenRepository
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.ExpiredResetCodeException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidResetCodeException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.TooManyAttemptsException
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.GetResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.ResetPasswordRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.VerifyResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.common.domain.events.user.UserEvent
import com.yehorsk.medicalplatformbackend.common.infra.EventPublisher
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.notification.service.PushNotificationService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Duration

@Service
class PasswordResetService(
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: StringRedisTemplate,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: EventPublisher,
    private val pushNotificationService: PushNotificationService,
    private val authEventService: AuthEventService
    ) {

    companion object {
        private const val OTP_TTL_MINUTES = 10L
        private const val MAX_ATTEMPTS = 5
    }

    private val secureRandom = SecureRandom()

    @Transactional
    fun requestReset(request: GetResetTokenRequestDto): ApiResponse{
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException()

        val otp = (secureRandom.nextInt(900_000) + 100_000).toString()
        val hashedToken = passwordEncoder.encode(otp)

        val codeKey = "reset:code:${user.id}"
        val attemptsKey = "reset:attempts:${user.id}"

        redisTemplate.opsForValue().set(codeKey, hashedToken!!, Duration.ofMinutes(OTP_TTL_MINUTES))
        redisTemplate.delete(attemptsKey)

        eventPublisher.publish(
            event = UserEvent.RequestResetPassword(
                userId = user.id!!,
                email = user.email,
                username = "${user.firstName} ${user.lastName}",
                passwordResetToken = otp,
                expiresInMinutes = OTP_TTL_MINUTES
            )
        )

        authEventService.record(user.id!!, AuditLogAction.PASSWORD_RESET_REQUESTED)

        return ApiResponse(message = "A reset code has been sent to your email")
    }

    @Transactional
    fun verifyCode(request: VerifyResetTokenRequestDto): ApiResponse{
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException()

        checkResetCode(user.id!!, request.code)
        return ApiResponse(message = "Code verified")
    }

    @Transactional
    fun resetPassword(request: ResetPasswordRequestDto): ApiResponse{
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException()

        checkResetCode(user.id!!, request.code)

        user.hashedPassword = passwordEncoder.encode(request.password)!!
        userRepository.save(user)
        refreshTokenRepository.deleteByUserId(user.id!!)
        pushNotificationService.unregisterAllDevices(user.id!!)

        redisTemplate.delete(listOf("reset:code:${user.id}", "reset:attempts:${user.id}"))
        authEventService.record(user.id!!, AuditLogAction.PASSWORD_RESET)
        return ApiResponse(message = "Password has been reset")
    }

    // Shared by verifyCode and resetPassword so both count towards MAX_ATTEMPTS
    private fun checkResetCode(userId: UserId, code: String) {
        val codeKey = "reset:code:$userId"
        val attemptsKey = "reset:attempts:$userId"

        val hashedToken = redisTemplate.opsForValue().get(codeKey)
            ?: run {
                recordCodeFailure(userId, AuditFailureReason.EXPIRED_RESET_CODE)
                throw ExpiredResetCodeException()
            }

        val attempts = redisTemplate.opsForValue().increment(attemptsKey) ?: 1
        redisTemplate.expire(attemptsKey, Duration.ofMinutes(OTP_TTL_MINUTES))

        if (attempts > MAX_ATTEMPTS) {
            redisTemplate.delete(listOf(codeKey, attemptsKey))
            recordCodeFailure(userId, AuditFailureReason.TOO_MANY_ATTEMPTS)
            throw TooManyAttemptsException()
        }

        if (!passwordEncoder.matches(code, hashedToken)) {
            recordCodeFailure(userId, AuditFailureReason.INVALID_RESET_CODE)
            throw InvalidResetCodeException()
        }
    }

    private fun recordCodeFailure(userId: UserId, reason: AuditFailureReason) {
        authEventService.record(userId, AuditLogAction.PASSWORD_RESET_CODE_CHECK, AuditLogOutcome.FAILED, reason)
    }

}