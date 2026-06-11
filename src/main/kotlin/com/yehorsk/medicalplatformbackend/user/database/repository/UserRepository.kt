package com.yehorsk.medicalplatformbackend.user.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, UserId> {

    fun existsByEmail(email: String): Boolean

    fun findUserEntityById(id: UserId): UserEntity?

    fun findByEmail(email: String): UserEntity?

}