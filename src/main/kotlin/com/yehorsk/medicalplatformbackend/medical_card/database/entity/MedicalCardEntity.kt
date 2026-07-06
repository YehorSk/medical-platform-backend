package com.yehorsk.medicalplatformbackend.medical_card.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.AllergenId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "medical_card")
class MedicalCardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MedicalCardId? = null,

    @Column(name = "blood_type", length = 3)
    var bloodType: String? = null,

    @Column(name = "insurance_number", nullable = true)
    var insuranceNumber: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: UserEntity? = null,

    @OneToMany(
        mappedBy = "medicalCard",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var allergens: MutableSet<PatientHasAllergenEntity> = mutableSetOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
){

    fun addAllergen(allergen: AllergenEntity, note: String? = null, severity: AllergySeverity){
        if(allergens.any{ it.allergen.id == allergen.id}) return
        allergens.add(
            PatientHasAllergenEntity(
                allergen = allergen,
                medicalCard = this,
                note = note,
                severity = severity
            )
        )
    }

    fun removerAllergen(allergenId: AllergenId){
        allergens.removeIf {
            it.allergen.id == allergenId
        }
    }

}