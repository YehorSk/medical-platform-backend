package com.yehorsk.medicalplatformbackend.doctor.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.ClinicId
import com.yehorsk.medicalplatformbackend.common.domain.type.WorkplaceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "clinics")
class ClinicEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: ClinicId? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var phone: String,

    @Column(nullable = false)
    var city: String,
)