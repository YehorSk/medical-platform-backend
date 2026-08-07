package com.yehorsk.medicalplatformbackend.appointments.controller

import com.yehorsk.medicalplatformbackend.appointments.service.AppointmentService
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.CreateAppointmentRequestDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.UpdateAppointmentStatusRequestDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentResponseDto
import com.yehorsk.medicalplatformbackend.common.api.config.IpRateLimit
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import org.springframework.http.ResponseEntity
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(
    private val appointmentService: AppointmentService
) {

    @PostMapping
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @IpRateLimit(
        requests = 30,
        duration = 1L,
        unit = TimeUnit.MINUTES
    )
    fun createAppointment(
        @RequestBody @Valid request: CreateAppointmentRequestDto
    ): ApiResponseWithData<AppointmentResponseDto> {
        return ApiResponseWithData(
            data = appointmentService.createAppointment(request),
            message = "Appointment created successfully"
        )
    }

    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun deleteAppointment(
        @PathVariable appointmentId: AppointmentId
    ): ResponseEntity<ApiResponse> {
        appointmentService.deleteAppointment(appointmentId)
        return ResponseEntity.ok(ApiResponse("Appointment deleted successfully"))
    }

    @PostMapping("/{appointmentId}/status")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun updateAppointmentStatus(
        @PathVariable appointmentId: AppointmentId,
        @RequestBody request: UpdateAppointmentStatusRequestDto
    ): ApiResponseWithData<AppointmentResponseDto> {
        return ApiResponseWithData(
            data = appointmentService.updateAppointmentStatus(request),
            message = "Appointment status updated successfully"
        )
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("isAuthenticated()")
    @IpRateLimit(
        requests = 60,
        duration = 1L,
        unit = TimeUnit.MINUTES
    )
    fun getMyAppointments(): ApiResponseWithData<List<AppointmentResponseDto>> {
        return ApiResponseWithData(
            data = appointmentService.getMyAppointments(),
            message = "Appointments retrieved successfully"
        )
    }

    @GetMapping("/upcoming")
    @PreAuthorize("isAuthenticated()")
    @IpRateLimit(
        requests = 60,
        duration = 1L,
        unit = TimeUnit.MINUTES
    )
    fun getUpcomingAppointments(): ApiResponseWithData<List<AppointmentResponseDto>> {
        return ApiResponseWithData(
            data = appointmentService.getUpcomingAppointments(),
            message = "Upcoming appointments retrieved successfully"
        )
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun getAppointmentById(
        @PathVariable appointmentId: AppointmentId
    ): ApiResponseWithData<AppointmentResponseDto> {
        return ApiResponseWithData(
            data = appointmentService.getAppointmentById(appointmentId),
            message = "Appointment retrieved successfully"
        )
    }
}




