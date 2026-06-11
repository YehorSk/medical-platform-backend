package com.yehorsk.medicalplatformbackend.medical_card.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasAllergenId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

enum class AllergySeverity {
    MILD, MODERATE, SEVERE
}

@Entity
@Table(name = "patient_has_allergens")
class PatientHasAllergenEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: PatientHasAllergenId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergens_id")
    var allergen: AllergenEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_card_id")
    var medicalCard: MedicalCardEntity,

    @Column(length = 45)
    var note: String? = null,

    @Enumerated(EnumType.STRING)
    var severity: AllergySeverity? = null
)