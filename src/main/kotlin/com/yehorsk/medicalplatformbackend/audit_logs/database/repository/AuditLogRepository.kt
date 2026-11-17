package com.yehorsk.medicalplatformbackend.audit_logs.database.repository

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogRepository : JpaRepository<AuditLogEntity, AuditLogId> {

    // accessedBy is fetched with the page to avoid one query per row
    @EntityGraph(attributePaths = ["accessedBy"])
    fun findAllByMedicalCardIdOrderByCreatedAtDesc(
        medicalCardId: MedicalCardId,
        pageable: Pageable
    ): Page<AuditLogEntity>
}
