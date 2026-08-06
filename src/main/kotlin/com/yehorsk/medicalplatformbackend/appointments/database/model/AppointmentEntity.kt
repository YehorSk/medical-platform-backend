package com.yehorsk.medicalplatformbackend.appointments.database.model

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.SpecializationEntity
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
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "appointments",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["doctor_id", "date_time"])
    ]
)
class AppointmentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: AppointmentId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    var doctor: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    var patient: UserEntity,

    @Enumerated(EnumType.STRING)
    var status: AppointmentStatus = AppointmentStatus.PENDING,

    var note: String = "",

    @Column(name = "date_time")
    var dateTime: Instant,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now(),

    )

enum class AppointmentStatus {
    PENDING, CONFIRMED, REJECTED, CANCELLED
}