package com.yehorsk.medicalplatformbackend.medical_card.controller

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.medical_card.service.MedicalCardService
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.request.UpdateMedicalCardRequestDto
import com.yehorsk.medicalplatformbackend.medical_card.service.dto.response.MedicalCardResponseDto
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/medical-cards")
class MedicalCardController(
    private val medicalCardService: MedicalCardService
) {

    @GetMapping("/{patientId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getByPatientId(@PathVariable patientId: UserId): ApiResponseWithData<MedicalCardResponseDto> {
        val data = medicalCardService.getMedicalCardByPatientId(patientId)
        return ApiResponseWithData(data)
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getMyMedicalCard(): ApiResponseWithData<MedicalCardResponseDto> {
        val data = medicalCardService.getMyMedicalCard()
        return ApiResponseWithData(data)
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun updateMyMedicalCard(@Valid @RequestBody request: UpdateMedicalCardRequestDto): ApiResponseWithData<MedicalCardResponseDto> {
        val data = medicalCardService.updateMyMedicalCard(request)
        return ApiResponseWithData(data)
    }

}
