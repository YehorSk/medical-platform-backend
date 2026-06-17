package com.yehorsk.medicalplatformbackend.doctor.controller

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.service.DoctorService
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.ChangeDoctorApprovalStatusDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/doctors")
class DoctorController(
    private val doctorService: DoctorService
) {
    @PostMapping("/specializations/{specializationId}")
    fun addSpecialization(
        @PathVariable specializationId: SpecializationId
    ): ResponseEntity<Void> {
        doctorService.addSpecialization(specializationId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/specializations/{specializationId}")
    fun removeSpecialization(
        @PathVariable specializationId: SpecializationId
    ): ResponseEntity<Void> {
        doctorService.removeSpecialization(specializationId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/approval")
    fun changeApprovalStatus(
        @RequestBody request: ChangeDoctorApprovalStatusDto
    ){
        doctorService.changeDoctorApprovalStatus(request)
    }
}
