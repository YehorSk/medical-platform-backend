package com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
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
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
    name = "patients_has_doctors",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["medical_card_id", "doctor_id"])
    ]
)
class PatientHasDoctorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: PatientHasDoctorId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_card_id")
    var medicalCard: MedicalCardEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    var doctor: UserEntity,

    @Enumerated(EnumType.STRING)
    var status: AccessStatus = AccessStatus.PENDING,

    @Enumerated(EnumType.STRING)
    var initiatedBy: UserRole,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),
){
    fun approve() {
        status = AccessStatus.APPROVED
    }

    fun revoke() {
        status = AccessStatus.REVOKED
    }

    fun reject() {
        status = AccessStatus.REJECTED
    }

    fun canApprove(): Boolean = status == AccessStatus.PENDING

    fun canReject(): Boolean = status == AccessStatus.PENDING

    fun canRevoke(): Boolean = status == AccessStatus.APPROVED

    fun isApproved(): Boolean = status == AccessStatus.APPROVED

    fun isActive(): Boolean = status == AccessStatus.APPROVED

    fun isPending(): Boolean = status == AccessStatus.PENDING

    fun isTerminated(): Boolean = status in setOf(AccessStatus.REJECTED, AccessStatus.REVOKED)
}

enum class AccessStatus {
    PENDING, APPROVED, REJECTED, REVOKED
}
