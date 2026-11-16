package com.yehorsk.medicalplatformbackend.medical_records.database.entity

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
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
 * Lifecycle of an uploaded file:
 * PENDING_SCAN -> AVAILABLE   (clean, moved from quarantine to the main bucket)
 * PENDING_SCAN -> INFECTED    (ClamAV found a virus, file deleted)
 * PENDING_SCAN -> SCAN_FAILED (scanner kept erroring; file stays in quarantine for a manual re-scan)
 * UPLOAD_FAILED               (file could not be stored at all; the doctor has to upload it again)
 */
enum class AttachmentStatus {
    PENDING_SCAN,
    AVAILABLE,
    INFECTED,
    SCAN_FAILED,
    UPLOAD_FAILED
}

@Entity
@Table(
    name = "medical_record_attachments",
    indexes = [
        Index(name = "idx_attachment_record", columnList = "medical_record_id"),
        Index(name = "idx_attachment_status_created", columnList = "status, created_at")
    ]
)
class MedicalRecordAttachmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: MedicalRecordAttachmentId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    var medicalRecord: MedicalRecordEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    var uploadedBy: UserEntity,

    @Column(name = "original_filename", nullable = false)
    var originalFilename: String,

    @Column(name = "content_type", nullable = false)
    var contentType: String,

    @Column(name = "size_bytes", nullable = false)
    var sizeBytes: Long,

    @Column(name = "sha256", nullable = false, length = 64)
    var sha256: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: AttachmentStatus = AttachmentStatus.PENDING_SCAN,

    @Column(name = "virus_name")
    var virusName: String? = null,

    @Column(name = "scan_attempts", nullable = false)
    var scanAttempts: Int = 0,

    @Column(name = "scanned_at")
    var scannedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant = Instant.now(),
)
