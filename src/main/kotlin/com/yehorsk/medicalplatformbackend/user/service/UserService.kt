package com.yehorsk.medicalplatformbackend.user.service

import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.InvalidCredentialsException
import com.yehorsk.medicalplatformbackend.auth.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.MessageResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.UserResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.user.service.dto.request.ChangePasswordRequestDto
import com.yehorsk.medicalplatformbackend.user.service.dto.request.UpdateUserRequestDto
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userProvider: CurrentUserProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @PreAuthorize("isAuthenticated()")
    fun me(): UserResponseDto {
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()
        return user.toUserResponseDto()
    }

    @PreAuthorize("isAuthenticated()")
    fun updateUserData(request: UpdateUserRequestDto): UserResponseDto {
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()

        user.firstName = request.firstName
        user.lastName = request.lastName
        user.title = request.title
        user.address = request.address
        user.phone = request.phone
        user.emergencyContactName = request.emergencyContactName
        user.emergencyContactPhone = request.emergencyContactPhone

        userRepository.save(user)
        return user.toUserResponseDto()
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    fun changePassword(request: ChangePasswordRequestDto): MessageResponseDto {
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()

        if (!passwordEncoder.matches(request.currentPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        user.hashedPassword = passwordEncoder.encode(request.password)!!
        userRepository.save(user)

        return MessageResponseDto("Password changed successfully")
    }

}