package com.yehorsk.medicalplatformbackend.medical_records.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.medical_records.database.entity.MedicalRecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MedicalRecordRepository : JpaRepository<MedicalRecordEntity, MedicalRecordId>

