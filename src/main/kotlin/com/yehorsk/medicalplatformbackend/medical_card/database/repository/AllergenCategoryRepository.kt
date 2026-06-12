package com.yehorsk.medicalplatformbackend.medical_card.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.AllergenCategoryId
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.AllergenCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AllergenCategoryRepository: JpaRepository<AllergenCategoryEntity, AllergenCategoryId> {
}