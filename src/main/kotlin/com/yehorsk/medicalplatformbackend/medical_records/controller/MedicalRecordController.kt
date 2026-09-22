package com.yehorsk.medicalplatformbackend.medical_records.controller

import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.MedicalRecordService
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.request.CreateMedicalRecordRequestDto
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/medical-records")
class MedicalRecordController(
    private val medicalRecordService: MedicalRecordService
) {

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    fun createMedicalRecord(
        @Valid @RequestBody request: CreateMedicalRecordRequestDto
    ): ApiResponse {
        return medicalRecordService.createMedicalRecord(request)
    }
}

