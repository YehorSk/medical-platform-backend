package com.yehorsk.medicalplatformbackend.medical_records.service.dto

import com.yehorsk.medicalplatformbackend.appointments.database.repository.AppointmentRepository
import com.yehorsk.medicalplatformbackend.appointments.exceptions.AppointmentNotFoundException
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordEntity
import com.yehorsk.medicalplatformbackend.medical_records.database.repository.MedicalRecordRepository
import com.yehorsk.medicalplatformbackend.medical_records.mappers.toBodyPartSelection
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.request.CreateMedicalRecordRequestDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class MedicalRecordService(
    private val medicalCardRepository: MedicalCardRepository,
    private val medicalRecordRepository: MedicalRecordRepository,
    private val appointmentRepository: AppointmentRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    @Transactional
    @PreAuthorize("hasRole('DOCTOR')")
    fun createMedicalRecord(request: CreateMedicalRecordRequestDto): ApiResponse{
        val currentUser = currentUserProvider.getCurrentUserEntity()

        val appointment = appointmentRepository.findByDoctorIdAndId(currentUser.id!!, request.appointmentId)
            ?: throw AppointmentNotFoundException()

        if (appointment.patient.id != request.patientId) {
            throw AppointmentNotFoundException()
        }

        val medicalCard = medicalCardRepository.findMedicalCardEntityByPatientId(request.patientId)
            ?: throw PatientNotFoundException()

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

        medicalRecordRepository.save(medicalRecord)

        return ApiResponse(
            message = "Medical record created successfully"
        )
    }

}