package com.yehorsk.medicalplatformbackend.doctor.controller

import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.service.SpecializationService
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.CreateSpecializationRequestDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.SpecializationResponseDto
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/specializations")
class SpecializationController(
    private val specializationService: SpecializationService
) {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getAll(): ApiResponseWithData<List<SpecializationResponseDto>> = specializationService.getAll()

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getById(@PathVariable id: SpecializationId): ApiResponseWithData<SpecializationResponseDto> =
        specializationService.getById(id)

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun create(@Valid @RequestBody request: CreateSpecializationRequestDto): ApiResponseWithData<SpecializationResponseDto> =
        specializationService.create(request)

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun update(@PathVariable id: SpecializationId, @Valid @RequestBody request: CreateSpecializationRequestDto): ApiResponseWithData<SpecializationResponseDto> =
        specializationService.update(id, request)

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun delete(@PathVariable id: SpecializationId): ApiResponse = specializationService.delete(id)

}

