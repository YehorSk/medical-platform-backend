package com.yehorsk.medicalplatformbackend.audit_logs.database.model

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
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
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "audit_logs")
class AuditLogEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: AuditLogId? = null,

    @Column(name = "accessed_by_id", nullable = false, updatable = false)
    var accessedById: UserId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "accessed_by_id",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var accessedBy: UserEntity,

    @Column(name = "medical_card_id", nullable = false, updatable = false)
    var medicalCardId: MedicalCardId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "medical_card_id",
        nullable = false,
        insertable = false,
        updatable = false
    )
    var medicalCard: MedicalCardEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var action: AuditLogAction,

    @Column(nullable = false, columnDefinition = "TEXT")
    var detail: String = "",

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),
)

enum class AuditLogAction {
    CREATE, READ, UPDATE, DELETE
}