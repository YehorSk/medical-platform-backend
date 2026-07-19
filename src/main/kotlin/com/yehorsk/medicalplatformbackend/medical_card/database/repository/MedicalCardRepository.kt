package com.yehorsk.medicalplatformbackend.medical_card.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MedicalCardRepository: JpaRepository<MedicalCardEntity, MedicalCardId> {

    fun findMedicalCardEntityByUserId(patientId: UserId): MedicalCardEntity?

}