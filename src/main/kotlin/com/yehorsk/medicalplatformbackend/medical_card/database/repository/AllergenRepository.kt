package com.yehorsk.medicalplatformbackend.medical_card.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.AllergenId
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.AllergenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AllergenRepository: JpaRepository<AllergenEntity, AllergenId> {
}