package com.yehorsk.medicalplatformbackend.doctor.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorHasSpecializationsId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "doctor_has_specializations")
class DoctorHasSpecializationsEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: DoctorHasSpecializationsId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    var doctor: DoctorEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    var specialization: SpecializationEntity,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    )