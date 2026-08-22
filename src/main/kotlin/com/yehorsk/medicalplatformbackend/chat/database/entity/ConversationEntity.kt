package com.yehorsk.medicalplatformbackend.chat.database.entity

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.ConversationId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "conversations",
    indexes = [
        Index(name = "idx_conversations_doctor_id", columnList = "doctor_id"),
        Index(name = "idx_conversations_patient_id", columnList = "patient_id"),
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_conversation_doctor_patient",
            columnNames = ["doctor_id", "patient_id"]
        )
    ]
)
class ConversationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: ConversationId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, updatable = false)
    var doctor: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, updatable = false)
    var patient: UserEntity,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @CreationTimestamp
    var createdAt: Instant = Instant.now(),
    var lastMessageAt: Instant? = null,
)
