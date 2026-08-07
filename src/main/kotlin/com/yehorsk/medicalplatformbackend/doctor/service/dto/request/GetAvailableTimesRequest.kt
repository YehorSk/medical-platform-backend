package com.yehorsk.medicalplatformbackend.doctor.service.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class GetAvailableTimesRequest(
    @field:NotNull
    val doctorId: DoctorId,

    @field:NotNull
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate
)

