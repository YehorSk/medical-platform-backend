package com.yehorsk.medicalplatformbackend.auth

import com.yehorsk.medicalplatformbackend.auth.service.AuthService
import com.yehorsk.medicalplatformbackend.auth.service.PasswordResetService
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.GetResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.LoginRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.RefreshTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.RegisterRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.ResetPasswordRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.request.VerifyResetTokenRequestDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.MessageResponseDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val pwdResetService: PasswordResetService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequestDto
    ): MessageResponseDto {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequestDto
    ): AuthenticatedUserResponseDto {
        return authService.login(request)
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequestDto
    ): AuthenticatedUserResponseDto {
        return authService.refresh(request.refreshToken)
    }

    @PostMapping("/logout")
    fun logout(): MessageResponseDto {
        authService.logout()
        return MessageResponseDto("Logged out successfully")
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(
        @Valid @RequestBody request: GetResetTokenRequestDto
    ): MessageResponseDto {
        pwdResetService.requestReset(request)
        return MessageResponseDto("If this email exists, a reset code has been sent")
    }

    @PostMapping("/verify-reset-code")
    fun verifyResetCode(
        @Valid @RequestBody request: VerifyResetTokenRequestDto
    ): MessageResponseDto {
        pwdResetService.verifyCode(request)
        return MessageResponseDto("Code verified")
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequestDto
    ): MessageResponseDto {
        pwdResetService.resetPassword(request)
        return MessageResponseDto("Password has been reset")
    }

}