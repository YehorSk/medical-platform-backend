package com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId

data class RelationIdRequest(
    val relationId: PatientHasDoctorId
)

