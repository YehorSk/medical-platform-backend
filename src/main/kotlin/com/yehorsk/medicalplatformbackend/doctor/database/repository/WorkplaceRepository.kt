package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.WorkplaceId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WorkplaceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WorkplaceRepository: JpaRepository<WorkplaceEntity, WorkplaceId> {

    fun findWorkplaceEntityById(workplaceId: WorkplaceId): WorkplaceEntity?

}

