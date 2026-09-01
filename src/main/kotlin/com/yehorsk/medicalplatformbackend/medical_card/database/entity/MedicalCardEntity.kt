package com.yehorsk.medicalplatformbackend.medical_card.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.AllergenId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardPatientDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
import java.time.LocalDate

@Entity
@Table(name = "medical_card")
class MedicalCardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MedicalCardId? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    var bloodType: BloodType? = null,

    @Column(name = "insurance_number", length = 255)
    var insuranceNumber: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var patient: UserEntity? = null,

    @OneToMany(
        mappedBy = "medicalCard",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var allergens: MutableSet<PatientHasAllergenEntity> = mutableSetOf(),

    @OneToMany(
        mappedBy = "medicalCard",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var doctors: MutableSet<PatientHasDoctorEntity> = mutableSetOf(),

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

enum class BloodType {
    A_POSITIVE,
    A_NEGATIVE,
    B_POSITIVE,
    B_NEGATIVE,
    AB_POSITIVE,
    AB_NEGATIVE,
    O_POSITIVE,
    O_NEGATIVE
}

enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}

fun UserEntity.toMedicalCardPatientDto() = MedicalCardPatientDto(
    id = id!!,
    firstName = firstName,
    lastName = lastName,
    title = title ?: ""
)