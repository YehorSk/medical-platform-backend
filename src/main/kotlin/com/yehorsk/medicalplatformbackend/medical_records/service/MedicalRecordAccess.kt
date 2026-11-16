package com.yehorsk.medicalplatformbackend.medical_records.service

import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordEntity
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus

// Author doctor, the patient who owns the card, or a doctor with APPROVED access to the patient's card
fun MedicalRecordEntity.canBeViewedBy(userId: UserId): Boolean =
    doctor.id == userId ||
            medicalCard.patient?.id == userId ||
            medicalCard.doctors.any { it.doctor.id == userId && it.status == AccessStatus.APPROVED }
