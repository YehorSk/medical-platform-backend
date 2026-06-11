package com.yehorsk.medicalplatformbackend.medical_card.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.AllergenId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "allergens")
class AllergenEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: AllergenId? = null,

    @Column(nullable = false, length = 45)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergen_category_id")
    var category: AllergenCategoryEntity
)