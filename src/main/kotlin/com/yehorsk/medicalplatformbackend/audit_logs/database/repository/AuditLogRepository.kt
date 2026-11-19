package com.yehorsk.medicalplatformbackend.audit_logs.database.repository

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogRepository : JpaRepository<AuditLogEntity, AuditLogId> {

    // accessedBy (and its doctor) are fetched with the page to avoid one query per row
    @EntityGraph(attributePaths = ["accessedBy", "accessedBy.doctor"])
    // Excludes the patient's own entries (also hides older rows logged before self-activity was dropped)
    fun findAllByMedicalCardIdAndAccessedByIdNotOrderByCreatedAtDesc(
        medicalCardId: MedicalCardId,
        excludedUserId: UserId,
        pageable: Pageable
    ): Page<AuditLogEntity>

    // Account activity of one user: the rows without a medical card (login, logout, password reset)
    fun findAllByAccessedByIdAndMedicalCardIdIsNullOrderByCreatedAtDesc(
        userId: UserId,
        pageable: Pageable
    ): Page<AuditLogEntity>
}
