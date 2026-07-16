package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.ClinicId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.ClinicEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ClinicRepository: JpaRepository<ClinicEntity, ClinicId> {

    fun findClinicEntityById(clinicId: ClinicId): ClinicEntity?

}

