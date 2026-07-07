package com.yehorsk.medicalplatformbackend.auth.service

import com.yehorsk.medicalplatformbackend.common.service.MailService
import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.ExpiredResetCodeException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidResetCodeException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.TooManyAttemptsException
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.GetResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.ResetPasswordRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.VerifyResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Duration

@Service
class PasswordResetService(
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: StringRedisTemplate,
    private val mailService: MailService,
    private val userRepository: UserRepository
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

        mailService.sendPlainText(to = request.email, subject = "OTP", body = otp)
        return ApiResponse(message = "If this email exists, a reset code has been sent")
    }

    @Transactional
    fun verifyCode(request: VerifyResetTokenRequestDto): ApiResponse{
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException()

        val codeKey = "reset:code:${user.id}"
        val attemptsKey = "reset:attempts:${user.id}"

        val hashedToken = redisTemplate.opsForValue().get(codeKey)
            ?: throw ExpiredResetCodeException()

        val attempts = redisTemplate.opsForValue().increment(attemptsKey) ?: 1
        redisTemplate.expire(attemptsKey, Duration.ofMinutes(OTP_TTL_MINUTES))

        if (attempts > MAX_ATTEMPTS) {
            redisTemplate.delete(listOf(codeKey, attemptsKey))
            throw TooManyAttemptsException()
        }

        if (!passwordEncoder.matches(request.code, hashedToken)) {
            throw InvalidResetCodeException()
        }
        return ApiResponse(message = "Code verified")
    }

    @Transactional
    fun resetPassword(request: ResetPasswordRequestDto): ApiResponse{
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException()


        val codeKey = "reset:code:${user.id}"
        val attemptsKey = "reset:attempts:${user.id}"

        val hashedToken = redisTemplate.opsForValue().get(codeKey)
            ?: throw ExpiredResetCodeException()

        if (!passwordEncoder.matches(request.code, hashedToken)) {
            throw InvalidResetCodeException()
        }

        user.hashedPassword = passwordEncoder.encode(request.password)!!
        userRepository.save(user)

        redisTemplate.delete(listOf(codeKey, attemptsKey))
        return ApiResponse(message = "Password has been reset")
    }

}