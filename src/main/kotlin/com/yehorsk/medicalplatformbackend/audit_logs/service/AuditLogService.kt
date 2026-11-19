package com.yehorsk.medicalplatformbackend.audit_logs.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.audit_logs.database.repository.AuditLogRepository
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun record(
        user: UserEntity,
        medicalCard: MedicalCardEntity,
        action: AuditLogAction,
        outcome: AuditLogOutcome,
        detail: String
    ) {
        auditLogRepository.save(
            AuditLogEntity(
                accessedById = requireNotNull(user.id),
                accessedBy = user,
                medicalCardId = requireNotNull(medicalCard.id),
                medicalCard = medicalCard,
                action = action,
                outcome = outcome,
                detail = detail
            )
        )
    }
}
