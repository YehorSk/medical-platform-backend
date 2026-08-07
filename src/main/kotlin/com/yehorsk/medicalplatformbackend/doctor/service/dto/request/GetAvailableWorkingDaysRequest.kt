package com.yehorsk.medicalplatformbackend.doctor.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class GetAvailableWorkingDaysRequest(
    @field:NotNull
    val doctorId: DoctorId,

    @field:NotNull
    @field:Min(1)
    @field:Max(12)
    val month: Int
)

