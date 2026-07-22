package com.yehorsk.medicalplatformbackend.doctor.controller

import com.yehorsk.medicalplatformbackend.auth.service.dto.response.PagedResponseDto
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.mappers.toDoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.DoctorService
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.ChangeDoctorApprovalStatusDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.GetDoctorsWithFilterDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorDetailsResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorResponseDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/doctors")
class DoctorController(
    private val doctorService: DoctorService
) {

    @GetMapping("/get-doctor")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getDoctor(
        @RequestParam(name = "doctorId") doctorId: DoctorId
    ): DoctorDetailsResponseDto {
        return doctorService.getDoctor(doctorId)
    }

    @PostMapping("/search")
    @PreAuthorize("isAuthenticated()")
    fun getAllDoctors(
        @RequestBody request: GetDoctorsWithFilterDto,
        @PageableDefault(size = 10) pageable: Pageable
    ): PagedResponseDto<DoctorResponseDto> {
        return doctorService.getAllDoctors(request, pageable)
    }

    @PostMapping("/specializations/{specializationId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun setSpecialization(
        @PathVariable specializationId: SpecializationId
    ): ResponseEntity<Void> {
        doctorService.setSpecialization(specializationId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/specializations/{specializationId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun removeSpecialization(
        @PathVariable specializationId: SpecializationId
    ): ResponseEntity<Void> {
//        doctorService.removeSpecialization(specializationId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/approval")
    @PreAuthorize("isAuthenticated()")
    fun changeApprovalStatus(
        @RequestBody request: ChangeDoctorApprovalStatusDto
    ){
        doctorService.changeDoctorApprovalStatus(request)
    }
}
