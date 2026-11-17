package com.yehorsk.medicalplatformbackend.audit_logs.controller

import com.yehorsk.medicalplatformbackend.audit_logs.service.PatientAuditLogService
import com.yehorsk.medicalplatformbackend.audit_logs.service.dto.response.PatientAuditLogResponseDto
import com.yehorsk.medicalplatformbackend.auth.service.dto.response.PagedResponseDto
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/audit-logs")
class AuditLogController(
    private val patientAuditLogService: PatientAuditLogService
) {

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getMyAuditLogs(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ApiResponseWithData<PagedResponseDto<PatientAuditLogResponseDto>> {
        return ApiResponseWithData(patientAuditLogService.getMyAuditLogs(page, size))
    }
}
