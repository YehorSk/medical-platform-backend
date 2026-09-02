package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorScheduleEntity
import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.appointments.exceptions.SlotNotAvailableException
import com.yehorsk.medicalplatformbackend.appointments.service.AppointmentService
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorScheduleRepository
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.service.dto.DayScheduleDto
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorDoesNotExistException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.InvalidScheduleException
import com.yehorsk.medicalplatformbackend.doctor.mappers.toDoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.UpdateScheduleRequestDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DayScheduleResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorScheduleResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.mappers.toDayScheduleResponseDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class DoctorScheduleService(
    private val doctorScheduleRepository: DoctorScheduleRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository
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
    fun getSchedule(doctorId: DoctorId): ApiResponseWithData<DoctorScheduleResponseDto> {
        val doctor = doctorRepository.findDoctorEntityById(doctorId) ?: throw DoctorDoesNotExistException()
        val schedules = doctorScheduleRepository.findAllByDoctorId(doctorId)

        return ApiResponseWithData(
            data = DoctorScheduleResponseDto(doctor.toDoctorResponseDto(), schedules.map { it.toDayScheduleResponseDto() })
        )
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getMySchedule(): ApiResponseWithData<DoctorScheduleResponseDto> {
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

    @Transactional
    fun getAvailableWorkingDaysForMonth(doctorId: DoctorId, month: Int): ApiResponseWithData<List<DayScheduleResponseDto>> {
        doctorRepository.findDoctorEntityById(doctorId) ?: throw DoctorDoesNotExistException()

        if (month !in 1..12) {
            throw InvalidScheduleException("Invalid month: $month")
        }

        val schedules = doctorScheduleRepository.findAllByDoctorId(doctorId)
        val working = schedules.filter { it.isWorkingDay }

        return ApiResponseWithData(
            data = working.map { it.toDayScheduleResponseDto() }
        )
    }

    @Transactional
    fun getAvailableTimesForDay(doctorId: DoctorId, date: LocalDate): ApiResponseWithData<List<String>> {
        val doctor = doctorRepository.findDoctorEntityById(doctorId)
            ?: throw DoctorDoesNotExistException()

        val weekDay = date.dayOfWeek.name.let { WeekDay.valueOf(it) }

        val schedule = doctorScheduleRepository.findByDoctorIdAndWeekDay(doctor.id!!, weekDay)
            ?: throw SlotNotAvailableException()

        if (!schedule.isWorkingDay) {
            throw SlotNotAvailableException()
        }

        val slots = mutableListOf<LocalTime>()
        var time = schedule.startTime!!
        val slotDuration = schedule.slotDurationMinutes
        val breakBetween = schedule.breakBetweenMinutes

        while (!time.plusMinutes(slotDuration.toLong()).isAfter(schedule.endTime)) {
            val slotEnd = time.plusMinutes(slotDuration.toLong())

            val overlapsLunch = schedule.lunchStart != null && schedule.lunchEnd != null &&
                    time.isBefore(schedule.lunchEnd) && slotEnd.isAfter(schedule.lunchStart)

            if (!overlapsLunch) {
                slots.add(time)
            }

            time = slotEnd.plusMinutes(breakBetween.toLong())
        }

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()

        val appointments = appointmentRepository.findAppointmentsByDoctorAndDateRange(doctor.id!!, startOfDay, endOfDay)
        val takenTimes = appointments.map { it.dateTime.atZone(ZoneId.systemDefault()).toLocalTime() }.toSet()

        val cutoffInstant = Instant.now().plus(AppointmentService.AppointmentConstants.BOOKING_BUFFER)

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val available = slots
            .filter { it !in takenTimes }
            .filter { slot ->
                val slotInstant = date.atTime(slot).atZone(ZoneId.systemDefault()).toInstant()
                slotInstant > cutoffInstant
            }
            .map { it.format(formatter) }

        return ApiResponseWithData(data = available)
    }

}