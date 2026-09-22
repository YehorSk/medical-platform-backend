package com.yehorsk.medicalplatformbackend.medical_records.database.entity

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
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
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.Instant

enum class BodyPart {
    RIGHT_HAND, LEFT_HAND, LEFT_FOREARM, RIGHT_FOREARM, RIGHT_UPPER_ARM, LEFT_UPPER_ARM, LEFT_SHOULDER, RIGHT_SHOULDER, NECK, HEAD, LEFT_KNEE, RIGHT_KNEE, LEFT_THIGH, RIGHT_THIGH, LEFT_FOOT, RIGHT_FOOT, LEFT_ANKLE, RIGHT_ANKLE, LEFT_LEG, RIGHT_LEG, LEFT_WRIST, RIGHT_WRIST, LEFT_ELBOW, RIGHT_ELBOW, CHEST, ABDOMEN, PELVIS, BACK, LOIN, BUTTOCKS, LEFT_ARM, RIGHT_ARM, LEFT_HAMSTRING, RIGHT_HAMSTRING, LEFT_CALF, RIGHT_CALF, LEFT_SOLE, RIGHT_SOLE
}

enum class BodyRegion {
    FRONT, BACK
}

data class BodyPartSelection(
    val bodyPart: BodyPart,
    val bodyRegion: BodyRegion
)

@Entity
@Table(name = "medical_records")
class MedicalRecordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MedicalRecordId? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    var diagnosis: String = "",

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String = "",

    @Column(name = "recommendations", columnDefinition = "TEXT")
    var recommendations: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    var doctor: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_card_id", nullable = false)
    var medicalCard: MedicalCardEntity,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    var appointment: AppointmentEntity? = null,

//    @OneToMany(
//        mappedBy = "medicalRecord",
//        cascade = [CascadeType.ALL],
//        orphanRemoval = true
//    )
//    var bodyParts: MutableSet<MedicalRecordHasBodyPartEntity> = mutableSetOf(),

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "body_parts", columnDefinition = "jsonb", nullable = false)
    var bodyParts: MutableSet<BodyPartSelection> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: MedicalRecordType,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
){
    fun addBodyPart(bodyPart: BodyPart, bodyRegion: BodyRegion) {
        bodyParts.add(BodyPartSelection(bodyPart, bodyRegion))
    }

    fun removeBodyPart(bodyPart: BodyPart, bodyRegion: BodyRegion) {
        bodyParts.removeIf { it.bodyPart == bodyPart && it.bodyRegion == bodyRegion }
    }
}

enum class MedicalRecordType {
    VISIT,
    LAB_RESULT,
    PRESCRIPTION,
    VACCINATION,
    PROCEDURE,
    DIAGNOSIS,
    CLINICAL_NOTE
}