package com.yehorsk.medicalplatformbackend.audit_logs.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.repository.AuditLogRepository
import com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response.AuditLogActorDto
import com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response.PatientAuditLogResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.PagedResponseDto
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PatientAuditLogService(
    private val auditLogRepository: AuditLogRepository,
    private val medicalCardRepository: MedicalCardRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    /** Audit entries for the current patient's medical card, newest first */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getMyAuditLogs(page: Int, size: Int): PagedResponseDto<PatientAuditLogResponseDto> {
        val patientId = currentUserProvider.getCurrentUserId()
        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(patientId)
            ?: throw PatientNotFoundException()

        val pageRequest = PageRequest.of(page.coerceAtLeast(0), size.coerceIn(1, MAX_PAGE_SIZE))
        val result = auditLogRepository.findAllByMedicalCardIdOrderByCreatedAtDesc(medicalCard.id!!, pageRequest)

        return PagedResponseDto(
            content = result.content.map { log ->
                val actor = log.accessedBy
                PatientAuditLogResponseDto(
                    id = log.id!!,
                    action = log.action,
                    outcome = log.outcome,
                    detail = log.detail,
                    createdAt = log.createdAt,
                    actor = AuditLogActorDto(
                        id = log.accessedById,
                        name = listOfNotNull(actor.title, actor.firstName, actor.lastName)
                            .filter { it.isNotBlank() }
                            .joinToString(" "),
                        role = actor.role,
                        isMe = log.accessedById == patientId
                    )
                )
            },
            page = result.number,
            size = result.size,
            hasNext = result.hasNext(),
            totalElements = result.totalElements,
            totalPages = result.totalPages
        )
    }

    private companion object {
        const val MAX_PAGE_SIZE = 50
    }
}
