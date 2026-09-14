package com.yehorsk.medicalplatformbackend.medical_records.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.BodyPartId
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.BodyPartEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BodyPartRepository : JpaRepository<BodyPartEntity, BodyPartId>

