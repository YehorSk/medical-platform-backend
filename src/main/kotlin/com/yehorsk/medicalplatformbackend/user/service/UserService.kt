package com.yehorsk.medicalplatformbackend.user.service

import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.user.service.dto.request.ChangePasswordRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.request.UpdateUserRequestDto
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userProvider: CurrentUserProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @PreAuthorize("isAuthenticated()")
    fun me(): ApiResponseWithData<UserResponseDto> {
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()
        return ApiResponseWithData(data = user.toUserResponseDto())
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    fun updateUserData(request: UpdateUserRequestDto): ApiResponseWithData<UserResponseDto> {
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()
        user.firstName = request.firstName
        user.lastName = request.lastName
        request.title?.let { user.title = it }
        request.address?.let { user.address = it }
        request.phone?.let { user.phone = it }
        request.emergencyContactName?.let { user.emergencyContactName = it }
        request.emergencyContactPhone?.let { user.emergencyContactPhone = it }

        val newUser = userRepository.save(user)

        return ApiResponseWithData(
            data = newUser.toUserResponseDto(),
            message = "Profile updated successfully"
        )
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    fun changePassword(request: ChangePasswordRequestDto): ApiResponse {
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()

        if (!passwordEncoder.matches(request.currentPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        user.hashedPassword = passwordEncoder.encode(request.password)!!
        userRepository.save(user)

        return ApiResponse(
            message = "Password changed successfully"
        )
    }

}