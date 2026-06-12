package com.yehorsk.medicalplatformbackend.doctor.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "doctors")
class DoctorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: DoctorId? = null,

    @Column(name = "license_number", nullable = false, unique = true)
    var licenseNumber: String,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    var user: UserEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: UserEntity? = null,

    @Column(nullable = false)
    var approved: Boolean = false,

    @OneToMany(
        mappedBy = "doctor",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var specializations: MutableSet<DoctorHasSpecializationsEntity> = mutableSetOf(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @Column(name = "approved_at")
    var approvedAt: Instant? = null,
){

    fun addSpecialization(specialization: SpecializationEntity){
        if(specializations.any { it.specialization.id == specialization.id }){
            return
        }
        specializations.add(
            DoctorHasSpecializationsEntity(
                doctor = this,
                specialization = specialization
            )
        )
    }

    fun removeSpecialization(specializationId: SpecializationId) {
        specializations.removeIf {
            it.specialization.id == specializationId
        }
    }

}