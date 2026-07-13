package com.yehorsk.medicalplatformbackend.patient_doctor_access.controller

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.PatientHasDoctorService
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response.PatientHasDoctorResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/patient-doctor-access")
class PatientHasDoctorController(
    private val service: PatientHasDoctorService
) {

    @PostMapping("/request/doctor/{doctorId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun patientRequestDoctor(
        @PathVariable doctorId: UserId
    ): ApiResponse {
        service.patientRequestDoctor(doctorId)
        return ApiResponse(message = "Access request sent to doctor")
    }

    @PostMapping("/request/patient/{patientId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun doctorRequestPatient(
        @PathVariable patientId: UserId
    ): ApiResponse {
        service.doctorRequestPatient(patientId)
        return ApiResponse(message = "Access request sent to patient")
    }

    @PostMapping("/approve/{relationId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun approveAccess(
        @PathVariable relationId: PatientHasDoctorId
    ): ApiResponse {
        service.approveAccess(relationId)
        return ApiResponse(message = "Access approved successfully")
    }

    @PostMapping("/revoke/{relationId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun revokeAccess(
        @PathVariable relationId: PatientHasDoctorId
    ): ApiResponse {
        service.revokeAccess(relationId)
        return ApiResponse(message = "Access revoked successfully")
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getPendingRequestsForDoctor(): ApiResponseWithData<List<PatientHasDoctorResponse>> {
        return ApiResponseWithData(
            data = service.getPendingRequestsForDoctor(),
            message = "Pending requests retrieved successfully"
        )
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun getAllRequests(): ApiResponseWithData<List<PatientHasDoctorResponse>> {
        return ApiResponseWithData(
            data = service.getAllRequests(),
            message = "All requests retrieved successfully"
        )
    }

    @GetMapping("/my-doctors")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getMyDoctors(): ApiResponseWithData<List<PatientHasDoctorResponse>> {
        return ApiResponseWithData(
            data = service.getMyDoctors(),
            message = "Your doctors retrieved successfully"
        )
    }

    @GetMapping("/my-patients")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getMyPatients(): ApiResponseWithData<List<PatientHasDoctorResponse>> {
        return ApiResponseWithData(
            data = service.getMyPatients(),
            message = "Your patients retrieved successfully"
        )
    }

}

