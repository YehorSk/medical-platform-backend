package com.yehorsk.medicalplatformbackend.auth.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.auth.database.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, Long> {

    fun findByUserIdAndHashedToken(userId: UserId, hashedToken: String): RefreshTokenEntity?

    fun deleteByUserIdAndHashedToken(userId: UserId, hashedToken: String)

    fun deleteByUserId(userId: UserId)

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt <= :now")
    fun deleteExpired(@Param("now") now: Instant): Int

}
