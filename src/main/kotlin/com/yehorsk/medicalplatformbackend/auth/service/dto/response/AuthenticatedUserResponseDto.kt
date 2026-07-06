package com.yehorsk.medicalplatformbackend.auth.service.dto.response


data class AuthenticatedUserResponseDto(
    val user: UserResponseDto,
    val accessToken: String,
    val refreshToken: String
)
