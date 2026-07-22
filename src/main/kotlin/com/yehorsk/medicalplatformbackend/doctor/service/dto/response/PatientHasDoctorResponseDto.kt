package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus

data class PatientHasDoctorResponseDto(
    val id: PatientHasDoctorId,
    val status: AccessStatus,
    val initiatedBy: UserRole,
    val createdAt: String,
    val updatedAt: String,
)