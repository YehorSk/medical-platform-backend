package com.yehorsk.medicalplatformbackend.common.security

import com.yehorsk.medicalplatformbackend.user.exceptions.types.UserNotAuthenticatedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class CurrentUserProvider {

    fun getCurrentUser(): UserDetails{
        val user = SecurityContextHolder.getContext().authentication?.principal as? UserDetails
            ?: throw UserNotAuthenticatedException()
        return user
    }

}