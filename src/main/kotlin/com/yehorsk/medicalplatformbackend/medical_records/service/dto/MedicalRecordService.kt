package com.yehorsk.medicalplatformbackend.medical_records.service.dto

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.appointments.exceptions.AppointmentNotFoundException
import com.yehorsk.medicalplatformbackend.appointments.service.AppointmentService
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordEntity
import com.yehorsk.medicalplatformbackend.medical_records.database.repository.MedicalRecordRepository
import com.yehorsk.medicalplatformbackend.medical_records.mappers.toBodyPartSelection
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.request.CreateMedicalRecordRequestDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class MedicalRecordService(
    private val medicalCardRepository: MedicalCardRepository,
    private val medicalRecordRepository: MedicalRecordRepository,
    private val appointmentRepository: AppointmentRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    private val logger = LoggerFactory.getLogger(MedicalRecordService::class.java)

    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    fun createMedicalRecord(request: CreateMedicalRecordRequestDto): ApiResponse{
        logger.info("Creating medical record: appointmentId=${request.appointmentId}, patientId=${request.patientId}")

        val currentUser = currentUserProvider.getCurrentUserEntity()

        val appointment = appointmentRepository.findByDoctorIdAndId(currentUser.id!!, request.appointmentId)
            ?: run {
                logger.warn (
                    "Appointment not found. doctorId=${currentUser.id}, " +
                            "appointmentId=${request.appointmentId}"
                    )
                throw AppointmentNotFoundException()
            }

        if (appointment.patient.id != request.patientId) {
            logger.warn (
                "Appointment patient mismatch. doctorId=${currentUser.id}, " +
                        "appointmentId=${request.appointmentId}, " +
                        "expectedPatientId=${request.patientId}, " +
                        "actualPatientId=${appointment.patient.id}"
            )
            throw AppointmentNotFoundException()
        }

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(request.patientId)
            ?: run {
                logger.warn(
                    "Medical card not found. patientId=${request.patientId}, " +
                            "doctorId=${currentUser.id}"
                )
                throw PatientNotFoundException()
            }

        val medicalRecord = MedicalRecordEntity(
            appointment = appointment,
            title = request.title,
            doctor = currentUser,
            medicalCard = medicalCard,
            type = request.type,
            diagnosis = request.diagnosis,
            recommendations = request.recommendations,
            bodyParts = request.bodyParts.map { it.toBodyPartSelection() }.toMutableSet()
        )

        appointment.status = AppointmentStatus.COMPLETED
        appointmentRepository.save(appointment)

        medicalRecordRepository.save(medicalRecord)

        return ApiResponse(
            message = "Medical record created successfully"
        )
    }

}