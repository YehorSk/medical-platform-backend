package com.yehorsk.medicalplatformbackend.user

import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.user.service.UserService
import com.yehorsk.medicalplatformbackend.user.service.dto.request.ChangePasswordRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.request.UpdateUserRequestDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun me(): ApiResponseWithData<UserResponseDto> {
        return userService.me()
    }

    @PostMapping("/update")
    fun updateUserData(@Valid @RequestBody request: UpdateUserRequestDto): ApiResponseWithData<UserResponseDto> {
        return userService.updateUserData(request)
    }

    @PostMapping("/change-password")
    fun changePassword(@Valid @RequestBody request: ChangePasswordRequestDto): ApiResponse {
        return userService.changePassword(request)
    }

}