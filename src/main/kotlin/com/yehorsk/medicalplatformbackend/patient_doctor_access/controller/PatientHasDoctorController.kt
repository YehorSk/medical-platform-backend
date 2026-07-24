package com.yehorsk.medicalplatformbackend.patient_doctor_access.controller

import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.PatientHasDoctorResponseDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.PatientHasDoctorService
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response.PatientHasDoctorResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.request.UserOrResIdRequest
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.request.RelationIdRequest

@RestController
@RequestMapping("/api/patient-doctor-access")
class PatientHasDoctorController(
    private val service: PatientHasDoctorService
) {

    @PostMapping("/request/doctor")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun patientRequestDoctor(
        @RequestBody request: UserOrResIdRequest
    ): ApiResponseWithData<PatientHasDoctorResponseDto> {
        val response = service.patientRequestDoctor(request.id)
        return ApiResponseWithData(data = response, message = "Access request sent to doctor")
    }

    @PostMapping("/request/patient")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun doctorRequestPatient(
        @RequestBody request: UserOrResIdRequest
    ): ApiResponseWithData<PatientHasDoctorResponseDto> {
        val response = service.doctorRequestPatient(request.id)
        return ApiResponseWithData(data = response, message = "Access request sent to patient")
    }

    @PostMapping("/give-access")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun patientGiveAccessToDoctor(
        @RequestBody request: UserOrResIdRequest
    ): ApiResponseWithData<PatientHasDoctorResponseDto> {
        val response = service.patientGiveAccessToDoctor(request.id)
        return ApiResponseWithData(data = response, message = "Access given to doctor")
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun approveAccess(
        @RequestBody request: UserOrResIdRequest
    ): ApiResponseWithData<PatientHasDoctorResponseDto> {
        val response = service.approveAccess(request.id)
        return ApiResponseWithData(data = response, message = "Access approved successfully")
    }

    @PostMapping("/reject")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun rejectAccess(
        @RequestBody request: UserOrResIdRequest
    ): ApiResponseWithData<PatientHasDoctorResponseDto> {
        val response = service.rejectAccess(request.id)
        return ApiResponseWithData(data = response, message = "Access rejected successfully")
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun revokeAccess(
        @RequestBody request: UserOrResIdRequest
    ): ApiResponseWithData<PatientHasDoctorResponseDto> {
        val response = service.revokeAccess(request.id)
        return ApiResponseWithData(data = response, message = "Access revoked successfully")
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
