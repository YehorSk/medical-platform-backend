package com.yehorsk.medicalplatformbackend.user.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.user.database.entity.ResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ResetTokenRepository : JpaRepository<ResetTokenEntity, Long>{

    fun findByUserId(userId: UserId): ResetTokenEntity?

    fun deleteByUserIdAndHashedToken(userId: UserId, hashedToken: String)

    fun deleteByUserId(userId: UserId)

}