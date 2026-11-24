package com.yehorsk.medicalplatformbackend.notification.infra.database

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.DeviceTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

interface DeviceTokenRepository: JpaRepository<DeviceTokenEntity, Long> {

    fun findByUserIdIn(userIds: List<UserId>): List<DeviceTokenEntity>

    fun findByToken(token: String): DeviceTokenEntity?

    // Derived deletes need a transaction; these are also called from the RabbitMQ listener thread, outside any service transaction
    @Transactional
    fun deleteByToken(token: String)

    @Transactional
    fun deleteByTokenAndUserId(token: String, userId: UserId)

    @Transactional
    fun deleteByUserId(userId: UserId)

    @Modifying
    @Query("""
        DELETE FROM DeviceTokenEntity d
        WHERE NOT EXISTS (
            SELECT 1 FROM RefreshTokenEntity r
            WHERE r.userId = d.userId
            AND r.expiresAt > :now
        )
    """)
    fun deleteForUsersWithoutActiveSession(@Param("now") now: Instant): Int

}
