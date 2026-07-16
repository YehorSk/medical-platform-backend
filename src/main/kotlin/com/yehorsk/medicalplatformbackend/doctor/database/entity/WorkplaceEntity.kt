package com.yehorsk.medicalplatformbackend.doctor.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.WorkplaceId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "workplaces")
class WorkplaceEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: WorkplaceId? = null,

    @Column(name = "room_number")
    var roomNumber: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    var clinic: ClinicEntity,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", unique = true)
    var doctor: DoctorEntity
)
