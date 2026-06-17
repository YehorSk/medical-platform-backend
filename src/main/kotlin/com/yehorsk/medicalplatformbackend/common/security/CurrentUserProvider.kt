package com.yehorsk.medicalplatformbackend.common.security

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.user.database.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserNotAuthenticatedException
import java.util.UUID

@Component
class CurrentUserProvider(
    private val userRepository: UserRepository
) {

    fun getCurrentUserId(): UserId {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        return principal as? UserId
            ?: throw UserNotAuthenticatedException()
    }

    fun getCurrentUserEntity(): UserEntity {
        val userId = getCurrentUserId()
        return userRepository.findById(userId)
            .orElseThrow { UserNotAuthenticatedException() }
    }
}