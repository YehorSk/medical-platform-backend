package com.yehorsk.medicalplatformbackend.appointments.service

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.appointments.exceptions.AppointmentAlreadyExistsException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.AppointmentNotFoundException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.InvalidAppointmentDateTimeException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.InvalidAppointmentStatusTransitionException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.SlotAlreadyBookedException
import com.yehorsk.medicalplatformbackend.appointments.exceptions.UnauthorizedException
import com.yehorsk.medicalplatformbackend.appointments.mappers.toAppointmentResponseDto
import com.yehorsk.medicalplatformbackend.appointments.mappers.toInstantAtTime
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.CreateAppointmentRequestDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.UpdateAppointmentStatusRequestDto
import com.yehorsk.medicalplatformbackend.appointments.service.dto.response.AppointmentResponseDto
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.DoctorNotFoundException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun createAppointment(request: CreateAppointmentRequestDto): AppointmentResponseDto {
        val currentUser = currentUserProvider.getCurrentUserEntity()

        val dateTime = request.date.toInstantAtTime(request.time)

        if (dateTime <= Instant.now()) {
            throw InvalidAppointmentDateTimeException()
        }

        if (appointmentRepository.existsByDoctorIdAndDateTime(
                request.doctorId,
                dateTime
            )
        ) {
            throw AppointmentAlreadyExistsException()
        }

        val doctor = userRepository.findUserEntityById(request.doctorId)
            ?: throw DoctorNotFoundException()
        if(doctor.role != UserRole.DOCTOR) {
            throw DoctorNotFoundException()
        }

        val appointment = AppointmentEntity(
            doctor = doctor,
            patient = currentUser,
            status = AppointmentStatus.PENDING,
            note = request.note,
            dateTime = dateTime
        )

        try {
            appointmentRepository.save(appointment)
        }   catch (e: DataIntegrityViolationException) {
            throw AppointmentAlreadyExistsException()
        }

        return appointment.toAppointmentResponseDto()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun deleteAppointment(appointmentId: AppointmentId) {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException() }

        val currentUser = currentUserProvider.getCurrentUserEntity()
        val isDoctor = appointment.doctor.id == currentUser.id
        val isPatient = appointment.patient.id == currentUser.id

        if (!isDoctor && !isPatient) {
            throw UnauthorizedException()
        }

        // Only allow deletion of PENDING appointments
        if (appointment.status != AppointmentStatus.PENDING) {
            appointment.status = AppointmentStatus.CANCELLED
        } else {
            appointmentRepository.delete(appointment)
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun updateAppointmentStatus(request: UpdateAppointmentStatusRequestDto): AppointmentResponseDto {
        val appointment = appointmentRepository.findById(request.appointmentId)
            .orElseThrow { AppointmentNotFoundException() }

        val currentUser = currentUserProvider.getCurrentUserEntity()
        if (appointment.doctor.id != currentUser.id) {
            throw UnauthorizedException()
        }

        validateStatusTransition(appointment.status, request.status)

        appointment.status = request.status
        if (request.note.isNotEmpty()) {
            appointment.note = request.note
        }

        return appointmentRepository.save(appointment).toAppointmentResponseDto()
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun getMyAppointments(): List<AppointmentResponseDto> {
        val currentUserId = currentUserProvider.getCurrentUserId()
        return appointmentRepository.findAllByPatientId(currentUserId)
            .map { it.toAppointmentResponseDto() }
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

        return appointment.toAppointmentResponseDto()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getUpcomingAppointments(): List<AppointmentResponseDto> {
        val currentUserId = currentUserProvider.getCurrentUserId()
        return appointmentRepository.findUpcomingAppointmentsByPatientId(currentUserId, Instant.now())
            .map { it.toAppointmentResponseDto() }
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
}












