package com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.user.database.entity.UserEntity
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
        UniqueConstraint(columnNames = ["patient_id", "doctor_id"])
    ]
)
class PatientHasDoctorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: PatientHasDoctorId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    var patient: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    var doctor: UserEntity,

    @Enumerated(EnumType.STRING)
    var status: AccessStatus = AccessStatus.PENDING,

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

    fun isApproved(): Boolean = status == AccessStatus.APPROVED
}

enum class AccessStatus {
    PENDING, APPROVED, REJECTED, REVOKED
}
