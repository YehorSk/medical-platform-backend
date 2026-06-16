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

    fun getCurrentUser(): UserDetails{
        val user = SecurityContextHolder.getContext().authentication?.principal as? UserDetails
            ?: throw UserNotAuthenticatedException()
        return user
    }

    fun getCurrentUserId(): UserId {
        val userIdString = getCurrentUser().username
        return UUID.fromString(userIdString)
    }

    fun getCurrentUserEntity(): UserEntity {
        val userId = getCurrentUserId()
        return userRepository.findById(userId)
            .orElseThrow { UserNotAuthenticatedException() }
    }

}