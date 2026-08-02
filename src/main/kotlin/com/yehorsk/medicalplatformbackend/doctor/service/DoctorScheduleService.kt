package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorScheduleEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorScheduleRepository
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.service.dto.DayScheduleDto
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorDoesNotExistException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.InvalidScheduleException
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.UpdateScheduleRequestDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DayScheduleResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.mappers.toDayScheduleResponseDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DoctorScheduleService(
    private val doctorRepository: DoctorRepository,
    private val doctorScheduleRepository: DoctorScheduleRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun updateSchedule(request: UpdateScheduleRequestDto): ApiResponse {

        val doctor = currentUserProvider.getCurrentUserEntity().doctor
            ?: throw DoctorDoesNotExistException()

        validateSchedule(request.schedules)

        doctorScheduleRepository.deleteAllByDoctorId(doctor.id!!)

        val newSchedules = request.schedules
            .filter { it.isWorkingDay }
            .map { dto ->
                DoctorScheduleEntity(
                    weekDay = dto.weekday,
                    startTime = dto.startTime!!,
                    endTime = dto.endTime!!,
                    lunchStart = dto.lunchStart,
                    lunchEnd = dto.lunchEnd,
                    slotDurationMinutes = dto.slotDurationMinutes ?: 30,
                    breakBetweenMinutes = dto.breakBetweenMinutes ?: 0,
                    doctor = doctor
                )
            }

        doctorScheduleRepository.saveAll(newSchedules)

        return ApiResponse(message = "Schedule updated successfully")
    }

    @Transactional
    fun getSchedule(doctorId: DoctorId): ApiResponseWithData<List<DayScheduleResponseDto>> {
        val schedules = doctorScheduleRepository.findAllByDoctorId(doctorId)

        return ApiResponseWithData(
            data = schedules.map { it.toDayScheduleResponseDto() }
        )
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getMySchedule(): ApiResponseWithData<List<DayScheduleResponseDto>> {
        val doctor = currentUserProvider.getCurrentUserEntity().doctor
            ?: throw DoctorDoesNotExistException()
        return getSchedule(doctor.id!!)
    }

    private fun validateSchedule(schedules: List<DayScheduleDto>) {
        val workingDays = schedules.filter { it.isWorkingDay }

        workingDays.forEach { day ->
            val start = day.startTime
                ?: throw InvalidScheduleException("Start time is required for working day ${day.weekday}")
            val end = day.endTime
                ?: throw InvalidScheduleException("End time is required for working day ${day.weekday}")

            if (!start.isBefore(end)) {
                throw InvalidScheduleException("Start time must be before end time for ${day.weekday}")
            }

            if (day.lunchStart != null && day.lunchEnd != null) {
                if (!day.lunchStart.isBefore(day.lunchEnd)) {
                    throw InvalidScheduleException("Lunch start must be before lunch end for ${day.weekday}")
                }
                if (day.lunchStart.isBefore(start) || day.lunchEnd.isAfter(end)) {
                    throw InvalidScheduleException("Lunch break must be within working hours for ${day.weekday}")
                }
            }

            val slotDuration = day.slotDurationMinutes ?: 30
            if (slotDuration <= 0) {
                throw InvalidScheduleException("Slot duration must be positive for ${day.weekday}")
            }
        }

        val duplicateWeekdays = workingDays
            .groupingBy { it.weekday }
            .eachCount()
            .filter { it.value > 1 }

        if (duplicateWeekdays.isNotEmpty()) {
            throw InvalidScheduleException("Duplicate schedule entries for: ${duplicateWeekdays.keys.joinToString()}")
        }
    }

}