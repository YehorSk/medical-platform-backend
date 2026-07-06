package com.yehorsk.medicalplatformbackend.auth.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.auth.database.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, Long> {

    fun findByUserIdAndHashedToken(userId: UserId, hashedToken: String): RefreshTokenEntity?

    fun deleteByUserIdAndHashedToken(userId: UserId, hashedToken: String)

    fun deleteByUserId(userId: UserId)

}