package com.yehorsk.medicalplatformbackend.doctor.controller

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.service.DoctorScheduleService
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.UpdateScheduleRequestDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DayScheduleResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.AvailableTimesResponseDto
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/schedules")
class DoctorScheduleController(
    private val doctorScheduleService: DoctorScheduleService
) {

    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun updateMySchedule(
        @Valid @RequestBody request: UpdateScheduleRequestDto
    ): ApiResponse {
        return doctorScheduleService.updateSchedule(request)
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getMySchedule(): ApiResponseWithData<List<DayScheduleResponseDto>> {
        return doctorScheduleService.getMySchedule()
    }

    @GetMapping("/{doctorId}")
    @PreAuthorize("isAuthenticated()")
    fun getDoctorSchedule(
        @PathVariable doctorId: DoctorId
    ): ApiResponseWithData<List<DayScheduleResponseDto>> {
        return doctorScheduleService.getSchedule(doctorId)
    }

    @GetMapping("/{doctorId}/available-days")
    @PreAuthorize("isAuthenticated()")
    fun getAvailableWorkingDays(
        @PathVariable doctorId: DoctorId,
        @RequestParam("month") month: Int
    ): ApiResponseWithData<List<DayScheduleResponseDto>> {
        return doctorScheduleService.getAvailableWorkingDaysForMonth(doctorId, month)
    }

    @GetMapping("/{doctorId}/available-times")
    @PreAuthorize("isAuthenticated()")
    fun getAvailableTimes(
        @PathVariable doctorId: DoctorId,
        @RequestParam("date") date: LocalDate
    ): ApiResponseWithData<AvailableTimesResponseDto> {
        val times = doctorScheduleService.getAvailableTimesForDay(doctorId, date)
        return ApiResponseWithData(
            data = AvailableTimesResponseDto(times.data)
        )
    }
}