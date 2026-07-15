package com.yehorsk.medicalplatformbackend.patient_doctor_access.controller

import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.PatientHasDoctorService
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response.PatientHasDoctorResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.request.UserIdRequest
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.request.RelationIdRequest

@RestController
@RequestMapping("/api/patient-doctor-access")
class PatientHasDoctorController(
    private val service: PatientHasDoctorService
) {

    @PostMapping("/request/doctor")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun patientRequestDoctor(
        @RequestBody request: UserIdRequest
    ): ApiResponse {
        service.patientRequestDoctor(request.userId)
        return ApiResponse(message = "Access request sent to doctor")
    }

    @PostMapping("/request/patient")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun doctorRequestPatient(
        @RequestBody request: UserIdRequest
    ): ApiResponse {
        service.doctorRequestPatient(request.userId)
        return ApiResponse(message = "Access request sent to patient")
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun approveAccess(
        @RequestBody request: RelationIdRequest
    ): ApiResponse {
        service.approveAccess(request.relationId)
        return ApiResponse(message = "Access approved successfully")
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun revokeAccess(
        @RequestBody request: RelationIdRequest
    ): ApiResponse {
        service.revokeAccess(request.relationId)
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
