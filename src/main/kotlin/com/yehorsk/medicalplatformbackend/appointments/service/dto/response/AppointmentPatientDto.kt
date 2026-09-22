package com.yehorsk.medicalplatformbackend.appointments.service.dto.response

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.BloodType
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.Gender

data class AppointmentPatientDto(
    val id: UserId,
    val firstName: String,
    val lastName: String,
    val title: String,
    // medical card related fields (optional)
    val dateOfBirth: String = "",
    val gender: Gender? = null,
    val bloodType: BloodType? = null
)