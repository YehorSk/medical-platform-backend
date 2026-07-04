package com.yehorsk.medicalplatformbackend.user.service

import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.user.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserDoesNotExistException
import com.yehorsk.medicalplatformbackend.user.service.dto.response.UserResponseDto
import com.yehorsk.medicalplatformbackend.user.service.mappers.toUserResponseDto
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userProvider: CurrentUserProvider,
    private val userRepository: UserRepository,
) {

    @PreAuthorize("isAuthenticated()")
    fun me(): UserResponseDto{
        val userId = userProvider.getCurrentUserId()
        val user = userRepository.findUserEntityById(userId)
            ?: throw UserDoesNotExistException()
        return user.toUserResponseDto()
    }

}