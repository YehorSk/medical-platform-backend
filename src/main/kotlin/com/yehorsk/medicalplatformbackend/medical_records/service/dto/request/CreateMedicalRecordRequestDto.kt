package com.yehorsk.medicalplatformbackend.medical_records.service.dto.request

import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.BodyPart
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.BodyRegion
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.jetbrains.annotations.NotNull

data class CreateMedicalRecordRequestDto(
    @field:NotNull
    val appointmentId: AppointmentId,

    @field:NotNull()
    val patientId: UserId,

    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Diagnosis is required")
    val diagnosis: String,

    val recommendations: String = "",

    @field:NotNull()
    val type: MedicalRecordType,

    @field:NotEmpty(message = "At least one body part must be selected")
    @field:Valid
    val bodyParts: Set<BodyPartSelectionDto> = emptySet(),
)

data class BodyPartSelectionDto(
    @field:NotNull()
    val bodyPart: BodyPart,

    @field:NotNull()
    val bodyRegion: BodyRegion,
)