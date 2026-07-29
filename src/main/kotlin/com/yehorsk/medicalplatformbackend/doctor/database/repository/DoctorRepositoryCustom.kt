package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.domain.Specification

interface DoctorRepositoryCustom {
    fun findAllSliced(spec: Specification<DoctorEntity>, pageable: Pageable): Slice<DoctorEntity>
}