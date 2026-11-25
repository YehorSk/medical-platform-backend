package com.yehorsk.medicalplatformbackend.audit_logs.service

import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditFailureReason
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogAction
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogEntity
import com.yehorsk.medicalplatformbackend.audit_logs.database.model.AuditLogOutcome
import com.yehorsk.medicalplatformbackend.audit_logs.database.repository.AuditLogRepository
import com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response.AuthEventResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.PagedResponseDto
import com.yehorsk.medicalplatformbackend.common.api.config.IpResolver
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/** Account activity (login, logout, password reset), stored in audit_logs without a medical card */
@Service
class AuthEventService(
    private val auditLogRepository: AuditLogRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val ipResolver: IpResolver,
    transactionManager: PlatformTransactionManager
) {

    private val logger = LoggerFactory.getLogger(AuthEventService::class.java)

    private val newTransaction = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }

    fun record(
        userId: UserId,
        action: AuditLogAction,
        outcome: AuditLogOutcome = AuditLogOutcome.SUCCESS,
        failureReason: AuditFailureReason? = null
    ) {
        try {
            val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
            val log = AuditLogEntity(
                accessedById = userId,
                action = action,
                outcome = outcome,
                failureReason = failureReason,
                ipAddress = request?.let { ipResolver.getClientIp(it) },
                userAgent = request?.getHeader("User-Agent")?.take(512)
            )
            newTransaction.executeWithoutResult { auditLogRepository.save(log) }
        } catch (e: Exception) {
            logger.error("Failed to record auth event {} for user {}", action, userId, e)
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    fun getMyAuthEvents(page: Int, size: Int): PagedResponseDto<AuthEventResponseDto> {
        val userId = currentUserProvider.getCurrentUserId()
        val pageRequest = PageRequest.of(page.coerceAtLeast(0), size.coerceIn(1, MAX_PAGE_SIZE))
        val result = auditLogRepository.findAllByAccessedByIdAndMedicalCardIdIsNullOrderByCreatedAtDesc(userId, pageRequest)

        return PagedResponseDto(
            content = result.content.map {
                AuthEventResponseDto(
                    id = it.id!!,
                    type = it.action,
                    outcome = it.outcome,
                    failureReason = it.failureReason,
                    ipAddress = it.ipAddress,
                    userAgent = it.userAgent,
                    createdAt = it.createdAt
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
