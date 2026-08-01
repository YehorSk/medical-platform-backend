package com.yehorsk.medicalplatformbackend.auth

import com.yehorsk.medicalplatformbackend.auth.service.AuthService
import com.yehorsk.medicalplatformbackend.auth.service.EmailVerificationService
import com.yehorsk.medicalplatformbackend.auth.service.PasswordResetService
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.EmailRequest
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.GetResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.LoginRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.RefreshTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.RegisterRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.ResetPasswordRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.VerifyEmailRequest
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.VerifyResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.common.api.config.EmailRateLimiter
import com.yehorsk.medicalplatformbackend.common.api.config.IpRateLimit
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val pwdResetService: PasswordResetService,
    private val emailVerificationService: EmailVerificationService,
    private val emailRateLimiter: EmailRateLimiter
) {

    @PostMapping("/register")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun register(
        @Valid @RequestBody request: RegisterRequestDto
    ): ApiResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    @IpRateLimit(
        requests = 10,
        duration = 15L,
        unit = TimeUnit.MINUTES
    )
    fun login(
        @Valid @RequestBody request: LoginRequestDto
    ): ApiResponseWithData<AuthenticatedUserResponseDto> {
        return authService.login(request)
    }

    @PostMapping("/refresh")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequestDto
    ): ApiResponseWithData<AuthenticatedUserResponseDto> {
        return authService.refresh(request.refreshToken)
    }

    @PostMapping("/logout")
    fun logout(): ApiResponse {
        return authService.logout()
    }

    @PostMapping("/forgot-password")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun forgotPassword(
        @Valid @RequestBody request: GetResetTokenRequestDto
    ): ApiResponse {
        return pwdResetService.requestReset(request)
    }

    @PostMapping("/verify-reset-code")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun verifyResetCode(
        @Valid @RequestBody request: VerifyResetTokenRequestDto
    ): ApiResponse {
        return pwdResetService.verifyCode(request)
    }

    @PostMapping("/reset-password")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequestDto
    ): ApiResponse {
        return pwdResetService.resetPassword(request)
    }

    @PostMapping("/resend")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun resendEmailVerification(
        @Valid @RequestBody request: EmailRequest
    ): ApiResponse {
        emailRateLimiter.withRateLimit(
            email = request.email,
            actionKey = "resend_verification"
        ) {
            emailVerificationService
                .resendEmailVerification(request.email)
        }
        return ApiResponse("If this email exists and is not verified, a verification link has been sent")
    }

    @PostMapping("/verify")
    @IpRateLimit(
        requests = 10,
        duration = 1L,
        unit = TimeUnit.HOURS
    )
    fun verifyEmail(
        @Valid @RequestBody request: VerifyEmailRequest
    ): ApiResponse {
        val response = emailVerificationService
            .verifyEmail(request.token)

        return response
    }

}