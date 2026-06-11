package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DoctorRepository: JpaRepository<DoctorEntity, DoctorId> {

    fun existsByLicenseNumber(number: String): Boolean

    fun findByLicenseNumber(number: String): DoctorEntity?

}