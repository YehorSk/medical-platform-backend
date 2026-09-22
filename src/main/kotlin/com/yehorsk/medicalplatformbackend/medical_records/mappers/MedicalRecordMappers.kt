package com.yehorsk.medicalplatformbackend.medical_records.mappers

import com.yehorsk.medicalplatformbackend.medical_records.database.entity.BodyPartSelection
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.request.BodyPartSelectionDto

fun BodyPartSelectionDto.toBodyPartSelection() = BodyPartSelection(
    bodyPart = this.bodyPart,
    bodyRegion = this.bodyRegion
)