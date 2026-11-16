package com.yehorsk.medicalplatformbackend.medical_records.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.AttachmentStatus
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.BodyPartSelection
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordType
import java.io.InputStream
import java.time.Instant

data class MedicalRecordCreatedResponseDto(
    val id: MedicalRecordId,
    val message: String,
    // Files start as PENDING_SCAN; poll GET /{id}/attachments for the scan result
    val attachments: List<AttachmentResponseDto> = emptyList()
)

data class AttachmentResponseDto(
    val id: MedicalRecordAttachmentId,
    val originalFilename: String,
    val contentType: String,
    val sizeBytes: Long,
    val status: AttachmentStatus,
    val virusName: String?,
    val createdAt: Instant,
    val scannedAt: Instant?
)

// Not serialized: carries an open S3 stream from the service to the controller
class AttachmentDownload(
    val filename: String,
    val contentType: String,
    val sizeBytes: Long,
    val content: InputStream
)

data class MedicalRecordDetailsResponseDto(
    val id: MedicalRecordId,
    val title: String,
    val type: MedicalRecordType,
    val diagnosis: String,
    val description: String,
    val recommendations: String,
    val bodyParts: List<BodyPartSelection>,
    val doctorId: UserId,
    val doctorName: String,
    val patientId: UserId?,
    val appointmentId: AppointmentId?,
    val createdAt: Instant,
    val updatedAt: Instant?,
    // Each file's status: PENDING_SCAN, AVAILABLE, INFECTED, SCAN_FAILED or UPLOAD_FAILED
    val attachments: List<AttachmentResponseDto>,
)
