package com.yehorsk.medicalplatformbackend.user

import com.yehorsk.medicalplatformbackend.user.service.UserService
import com.yehorsk.medicalplatformbackend.user.service.dto.response.UserResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun me(): UserResponseDto {
        return userService.me()
    }

}