package com.yehorsk.medicalplatformbackend.medical_records.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.AttachmentStatus
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordAttachmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface MedicalRecordAttachmentRepository : JpaRepository<MedicalRecordAttachmentEntity, MedicalRecordAttachmentId> {

    fun findAllByMedicalRecordIdOrderByCreatedAtAsc(medicalRecordId: MedicalRecordId): List<MedicalRecordAttachmentEntity>

    fun findByIdAndMedicalRecordId(
        id: MedicalRecordAttachmentId,
        medicalRecordId: MedicalRecordId
    ): MedicalRecordAttachmentEntity?

    fun findAllByStatusAndCreatedAtBefore(status: AttachmentStatus, before: Instant): List<MedicalRecordAttachmentEntity>
}
