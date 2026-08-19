package com.yehorsk.medicalplatformbackend.appointments.service

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.appointments.exceptions.AppointmentNotFoundException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.InvalidAppointmentDateTimeException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.InvalidAppointmentStatusException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.InvalidAppointmentStatusTransitionException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.SlotAlreadyBookedException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.SlotNotAvailableException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.UnauthorizedException
import com.yehorsk.medicalplatformbackend.appointments.mappers.toAdminAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.mappers.toDoctorAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.mappers.toInstantAtTime
import com.yehorsk.medicalplatformbackend.appointments.mappers.toPatientAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.CreateRescheduleAppointmentRequestDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.UpdateAppointmentStatusRequestDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentResponseDto
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorScheduleRepository
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.DoctorNotFoundException
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Service
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val doctorScheduleRepository: DoctorScheduleRepository
) {

    private val logger = LoggerFactory.getLogger(AppointmentService::class.java)

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun createOrRescheduleAppointment(request: CreateRescheduleAppointmentRequestDto): AppointmentResponseDto {
        val currentUser = currentUserProvider.getCurrentUserEntity()

        val doctor = doctorRepository.findDoctorEntityById(request.doctorId)
            ?: throw DoctorNotFoundException()

        val dateTime = request.date.toInstantAtTime(request.time)

        if (dateTime <= Instant.now()) {
            throw InvalidAppointmentDateTimeException()
        }

        validateSlotIsWithinSchedule(request.doctorId, request.date, request.time)

        val appointment = AppointmentEntity(
            doctor = doctor.user!!,
            patient = currentUser,
            status = AppointmentStatus.PENDING,
            note = request.note,
            dateTime = dateTime
        )

        try {
            appointmentRepository.saveAndFlush(appointment)
        } catch (_: DataIntegrityViolationException) {
            logger.warn("Slot already booked for doctorId={}, dateTime={}", request.doctorId, dateTime)
            throw SlotAlreadyBookedException()
        }

        return appointment.toPatientAppointmentResponseDto()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun cancelAppointment(appointmentId: AppointmentId): AppointmentResponseDto {
        val currentUser = currentUserProvider.getCurrentUserEntity()

        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException() }

        val isDoctor = appointment.doctor.id == currentUser.id
        val isPatient = appointment.patient.id == currentUser.id

        if (!isDoctor && !isPatient) {
            throw UnauthorizedException()
        }

        try {
            appointment.cancel()
        } catch (e: IllegalStateException) {
            throw InvalidAppointmentStatusException()
        }

        appointmentRepository.save(appointment)

        return when (currentUser.role) {

            UserRole.DOCTOR -> appointment.toDoctorAppointmentResponseDto()

            UserRole.PATIENT -> appointment.toPatientAppointmentResponseDto()

            UserRole.ADMIN -> appointment.toAdminAppointmentResponseDto()
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun updateAppointmentStatus(request: UpdateAppointmentStatusRequestDto): AppointmentResponseDto {
        val currentUser = currentUserProvider.getCurrentUserEntity()

        val appointment = appointmentRepository.findByDoctorIdAndId(currentUser.id!!, request.appointmentId)
            ?: throw AppointmentNotFoundException()

        validateStatusTransition(appointment.status, request.status)

        appointment.status = request.status
        if (request.note.isNotEmpty()) {
            appointment.note = request.note
        }

        return appointmentRepository.save(appointment).toDoctorAppointmentResponseDto()
    }

    @Transactional()
    @PreAuthorize("isAuthenticated()")
    fun getMyAppointments(): List<AppointmentResponseDto> {

        val currentUser = currentUserProvider.getCurrentUserEntity()

        return when (currentUser.role) {

            UserRole.DOCTOR ->
                appointmentRepository
                    .findAllByDoctorId(currentUser.id!!)
                    .map { it.toDoctorAppointmentResponseDto() }

            UserRole.PATIENT ->
                appointmentRepository
                    .findAllByPatientId(currentUser.id!!)
                    .map { it.toPatientAppointmentResponseDto() }

            UserRole.ADMIN ->
                appointmentRepository
                    .findAll()
                    .map { it.toAdminAppointmentResponseDto() }
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun getAppointmentById(appointmentId: AppointmentId): AppointmentResponseDto {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException() }

        val currentUser = currentUserProvider.getCurrentUserEntity()
        val isDoctor = appointment.doctor.id == currentUser.id
        val isPatient = appointment.patient.id == currentUser.id

        if (!isDoctor && !isPatient) {
            throw UnauthorizedException()
        }

        return when (currentUser.role) {

            UserRole.DOCTOR -> appointment.toDoctorAppointmentResponseDto()

            UserRole.PATIENT -> appointment.toPatientAppointmentResponseDto()

            UserRole.ADMIN -> appointment.toAdminAppointmentResponseDto()
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun getUpcomingAppointments(): List<AppointmentResponseDto> {
        val currentUser = currentUserProvider.getCurrentUserEntity()
        return appointmentRepository
            .findUpcomingAppointmentsByPatientId(currentUser.id!!, Instant.now())
            .map { it.toDoctorAppointmentResponseDto() }
    }

    private fun validateStatusTransition(currentStatus: AppointmentStatus, newStatus: AppointmentStatus) {
        val validTransitions = mapOf(
            AppointmentStatus.PENDING to listOf(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.REJECTED,
                AppointmentStatus.CANCELLED
            ),
            AppointmentStatus.CONFIRMED to listOf(AppointmentStatus.CANCELLED),
            AppointmentStatus.REJECTED to listOf(),
            AppointmentStatus.CANCELLED to listOf()
        )

        val allowedTransitions = validTransitions[currentStatus] ?: emptyList()
        if (newStatus !in allowedTransitions) {
            throw InvalidAppointmentStatusTransitionException(currentStatus.name, newStatus.name)
        }
    }

    private fun validateSlotIsWithinSchedule(doctorId: DoctorId, date: LocalDate, time: LocalTime) {
        logger.debug("Validating slot for doctorId={}, date={}, time={}", doctorId, date, time)

        val weekDay = WeekDay.valueOf(date.dayOfWeek.name)

        val schedule = doctorScheduleRepository.findByDoctorIdAndWeekDay(doctorId, weekDay)
            ?: run {
                logger.warn("No schedule found for doctorId={} on weekDay={}", doctorId, weekDay)
                throw SlotNotAvailableException()
            }

        logger.debug("Schedule for doctorId={}, weekDay={} -> isWorkingDay={}, startTime={}, endTime={}, slotDurationMinutes={}, lunchStart={}, lunchEnd={}",
            doctorId, weekDay, schedule.isWorkingDay, schedule.startTime, schedule.endTime, schedule.slotDurationMinutes, schedule.lunchStart, schedule.lunchEnd)

        if (!schedule.isWorkingDay) {
            logger.warn("Requested day is not a working day for doctorId={}, weekDay={}", doctorId, weekDay)
            throw SlotNotAvailableException()
        }

        val slotEnd = time.plusMinutes(schedule.slotDurationMinutes.toLong())

        val startTime = schedule.startTime
        val endTime = schedule.endTime
        if (startTime == null || endTime == null) {
            logger.error("Schedule for doctorId={} weekDay={} is missing start or end time (startTime={}, endTime={})", doctorId, weekDay, startTime, endTime)
            throw SlotNotAvailableException()
        }

        if (time.isBefore(startTime) || slotEnd.isAfter(endTime)) {
            logger.warn("Requested slot out of working hours for doctorId={}, date={}, time={}, slotEnd={}, startTime={}, endTime={}",
                doctorId, date, time, slotEnd, startTime, endTime)
            throw SlotNotAvailableException()
        }

        val overlapsLunch = schedule.lunchStart != null && schedule.lunchEnd != null &&
                time.isBefore(schedule.lunchEnd) && slotEnd.isAfter(schedule.lunchStart)

        if (overlapsLunch) {
            logger.warn("Requested slot overlaps lunch for doctorId={}, date={}, time={}, lunchStart={}, lunchEnd={}",
                doctorId, date, time, schedule.lunchStart, schedule.lunchEnd)
            throw SlotNotAvailableException()
        }

        val minutesFromStart = Duration.between(startTime, time).toMinutes()
        if (minutesFromStart % schedule.slotDurationMinutes != 0L) {
            logger.warn("Requested slot is misaligned with schedule for doctorId={}, date={}, time={}, minutesFromStart={}, slotDurationMinutes={}",
                doctorId, date, time, minutesFromStart, schedule.slotDurationMinutes)
            throw SlotNotAvailableException()
        }

        logger.debug("Slot validated successfully for doctorId={}, date={}, time={}", doctorId, date, time)
    }
}












