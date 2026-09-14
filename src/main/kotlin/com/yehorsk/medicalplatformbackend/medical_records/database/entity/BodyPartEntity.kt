package com.yehorsk.medicalplatformbackend.medical_records.database.entity

import com.yehorsk.medicalplatformbackend.common.domain.type.BodyPartId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

enum class BodyPartCategory {
    HEAD, TORSO, ARMS, LEGS, TEETH
}

@Entity
@Table(name = "body_parts")
class BodyPartEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: BodyPartId? = null,

    @Column(nullable = false, length = 45, unique = true)
    var code: String,

    @Column(nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: BodyPartCategory
)

