package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.SpecializationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpecializationRepository: JpaRepository<SpecializationEntity, SpecializationId> {

    fun findSpecializationEntitiesById(specializationId: SpecializationId): SpecializationEntity?

}