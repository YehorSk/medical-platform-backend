package com.yehorsk.medicalplatformbackend.patient_doctor_access.service.mappers

import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response.PatientHasDoctorResponse
import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import com.yehorsk.medicalplatformbackend.medical_card.service.mappers.toMedicalCardResponseDto

fun PatientHasDoctorEntity.toPatientHasDoctorResponse() = PatientHasDoctorResponse(
    id = id!!,
    medicalCard = medicalCard.toMedicalCardResponseDto(),
    doctor = doctor.toUserResponseDto(),
    status = status,
    createdAt = createdAt
)