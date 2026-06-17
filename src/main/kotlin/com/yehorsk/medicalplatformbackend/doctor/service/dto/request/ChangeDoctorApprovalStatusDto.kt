package com.yehorsk.medicalplatformbackend.doctor.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId

data class ChangeDoctorApprovalStatusDto(
    val doctorId: DoctorId,
    val status: Boolean
)