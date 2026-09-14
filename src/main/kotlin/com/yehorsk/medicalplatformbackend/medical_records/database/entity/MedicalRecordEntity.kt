package com.yehorsk.medicalplatformbackend.medical_records.database.entity

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.BodyPartId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
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
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "medical_records")
class MedicalRecordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MedicalRecordId? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    var doctor: DoctorEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_card_id", nullable = false)
    var medicalCard: MedicalCardEntity,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true)
    var appointment: AppointmentEntity? = null,

    @OneToMany(
        mappedBy = "medicalRecord",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var bodyParts: MutableSet<MedicalRecordHasBodyPartEntity> = mutableSetOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
) {
    fun addBodyPart(bodyPart: BodyPartEntity) {
        if (bodyParts.any { it.bodyPart.id == bodyPart.id }) return
        bodyParts.add(
            MedicalRecordHasBodyPartEntity(
                medicalRecord = this,
                bodyPart = bodyPart
            )
        )
    }

    fun removeBodyPart(bodyPartId: BodyPartId) {
        bodyParts.removeIf {
            it.bodyPart.id == bodyPartId
        }
    }
}

