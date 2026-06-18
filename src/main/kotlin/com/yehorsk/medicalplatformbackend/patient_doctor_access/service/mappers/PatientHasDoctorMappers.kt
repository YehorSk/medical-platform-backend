package com.yehorsk.medicalplatformbackend.patient_doctor_access.service.mappers

import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response.PatientHasDoctorResponse
import com.yehorsk.medicalplatformbackend.user.service.mappers.toUserResponseDto

fun PatientHasDoctorEntity.toPatientHasDoctorResponse() = PatientHasDoctorResponse(
    id = id!!,
    patient = patient.toUserResponseDto(),
    doctor = doctor.toUserResponseDto(),
    status = status,
    createdAt = createdAt
)