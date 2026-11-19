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
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

/**
 * One table for all audited activity:
 * - medical card access (medicalCardId set, accessedBy = who accessed it)
 * - account activity: login, logout, password reset (medicalCardId null, accessedBy = the account owner)
 */
@Entity
@Table(
    name = "audit_logs",
    indexes = [
        Index(name = "idx_audit_logs_card_created", columnList = "medical_card_id, created_at"),
        Index(name = "idx_audit_logs_user_created", columnList = "accessed_by_id, created_at")
    ]
)
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
    var accessedBy: UserEntity? = null,

    /** Null for account activity (login, logout, password reset) */
    @Column(name = "medical_card_id", updatable = false)
    var medicalCardId: MedicalCardId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "medical_card_id",
        insertable = false,
        updatable = false
    )
    var medicalCard: MedicalCardEntity? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    var action: AuditLogAction,

    // Default lets ddl-auto add the column to a table that already has rows
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) NOT NULL DEFAULT 'SUCCESS'")
    var outcome: AuditLogOutcome = AuditLogOutcome.SUCCESS,

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 40)
    var failureReason: AuditFailureReason? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var detail: String = "",

    @Column(name = "ip_address", length = 64)
    var ipAddress: String? = null,

    @Column(name = "user_agent", length = 512)
    var userAgent: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),
)

enum class AuditLogAction {
    CREATE, READ, UPDATE, DELETE,

    LOGIN,
    LOGOUT,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_RESET_CODE_CHECK,
    PASSWORD_RESET
}

enum class AuditLogOutcome {
    SUCCESS,
    DENIED,
    FAILED
}

enum class AuditFailureReason {
    WRONG_PASSWORD,
    EMAIL_NOT_VERIFIED,
    DOCTOR_NOT_APPROVED,
    INVALID_RESET_CODE,
    EXPIRED_RESET_CODE,
    TOO_MANY_ATTEMPTS
}
