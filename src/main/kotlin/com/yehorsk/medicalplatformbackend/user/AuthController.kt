package com.yehorsk.medicalplatformbackend.user

import com.yehorsk.medicalplatformbackend.user.service.AuthService
import com.yehorsk.medicalplatformbackend.user.service.dto.request.LoginRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.request.RefreshTokenRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.request.RegisterRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.response.AuthenticatedUserResponseDto
import com.yehorsk.medicalplatformbackend.user.service.dto.response.RegisterResponseDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/")
//    fun user(@AuthenticationPrincipal principal: CustomUserDetails): UserResponseDto {
//        return principal.user.toUserResponseDto()
//    }

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequestDto
    ): RegisterResponseDto {
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
    fun logout(
        @Valid @RequestBody request: RefreshTokenRequestDto
    ) {
        authService.logout(request.refreshToken)
    }

}