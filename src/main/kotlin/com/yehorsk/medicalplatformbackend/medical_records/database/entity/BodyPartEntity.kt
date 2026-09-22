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

//enum class BodyPart {
//    RIGHT_HAND, LEFT_HAND, LEFT_FOREARM, RIGHT_FOREARM, RIGHT_UPPER_ARM, LEFT_UPPER_ARM, LEFT_SHOULDER, RIGHT_SHOULDER, NECK, HEAD, LEFT_KNEE, RIGHT_KNEE, LEFT_THIGH, RIGHT_THIGH, LEFT_FOOT, RIGHT_FOOT, LEFT_ANKLE, RIGHT_ANKLE, LEFT_LEG, RIGHT_LEG, LEFT_WRIST, RIGHT_WRIST, LEFT_ELBOW, RIGHT_ELBOW, CHEST, ABDOMEN, PELVIS, BACK, LOIN, BUTTOCKS, LEFT_ARM, RIGHT_ARM, LEFT_HAMSTRING, RIGHT_HAMSTRING, LEFT_CALF, RIGHT_CALF, LEFT_SOLE, RIGHT_SOLE
//}
//
//enum class BodyRegion {
//    FRONT, BACK
//}
//
//@Entity
//@Table(name = "body_parts")
//class BodyPartEntity(
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    var id: BodyPartId? = null,
//
//    @Column(nullable = false, length = 100)
//    var name: String,
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "body_part", nullable = false)
//    var bodyPart: BodyPart,
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "body_region", nullable = false)
//    var bodyRegion: BodyRegion
//)
//
