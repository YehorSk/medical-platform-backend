package com.yehorsk.medicalplatformbackend.user.service.dto.response


data class AuthenticatedUserResponseDto(
    val user: UserResponseDto,
    val accessToken: String,
    val refreshToken: String
)
