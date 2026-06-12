package com.yehorsk.medicalplatformbackend.doctor.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "specializations")
class SpecializationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: SpecializationId? = null,

    @Column(name = "name", nullable = false, unique = true)
    var name: String,

    @OneToMany(
        mappedBy = "specialization",
        fetch = FetchType.LAZY
    )
    val doctors: MutableSet<DoctorHasSpecializationsEntity> = mutableSetOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)
