package com.yehorsk.medicalplatformbackend.medical_card.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.AllergenCategoryId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "allergen_category")
class AllergenCategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: AllergenCategoryId? = null,

    @Column(nullable = false)
    var name: String
)