package com.yehorsk.medicalplatformbackend.notification.infra.database

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.notification.infra.database.model.DeviceTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceTokenRepository: JpaRepository<DeviceTokenEntity, Long> {

    fun findByUserIdIn(userIds: List<UserId>): List<DeviceTokenEntity>

    fun findByToken(token: String): DeviceTokenEntity?

    fun deleteByToken(token: String)

}
