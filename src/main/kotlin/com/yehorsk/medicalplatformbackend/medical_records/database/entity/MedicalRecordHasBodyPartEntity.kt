package com.yehorsk.medicalplatformbackend.medical_records.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordHasBodyPartId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "medical_record_has_body_parts")
class MedicalRecordHasBodyPartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MedicalRecordHasBodyPartId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    var medicalRecord: MedicalRecordEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_part_id")
    var bodyPart: BodyPartEntity
)

