package com.yehorsk.medicalplatformbackend.audit_logs.database.repository

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.AuditLogId
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogRepository : JpaRepository<AuditLogEntity, AuditLogId>

