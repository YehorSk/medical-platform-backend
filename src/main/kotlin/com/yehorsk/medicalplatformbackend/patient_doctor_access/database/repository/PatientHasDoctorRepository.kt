package com.yehorsk.medicalplatformbackend.patient_doctor_access.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalCardId
import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PatientHasDoctorRepository: JpaRepository<PatientHasDoctorEntity, PatientHasDoctorId> {

    fun findByMedicalCardIdAndDoctorId(
        medicalCardId: MedicalCardId,
        doctorId: UserId
    ): PatientHasDoctorEntity?

    @Query("""
    SELECT COUNT(phd) > 0
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.id = :patientId
      AND phd.doctor.id = :doctorId
      AND phd.status IN ('PENDING', 'APPROVED')
    """)
    fun existsActiveRelation(
        @Param("patientId") patientId: MedicalCardId,
        @Param("doctorId") doctorId: UserId
    ): Boolean

    @Query("""
    SELECT phd
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.id = :patientId
      AND phd.doctor.id = :doctorId
      AND phd.status IN ('PENDING', 'APPROVED')
    """)
    fun getActiveRelation(
        @Param("patientId") patientId: MedicalCardId,
        @Param("doctorId") doctorId: UserId
    ): PatientHasDoctorEntity?

    fun findPatientHasDoctorEntityById(
        patientHasDoctorId: PatientHasDoctorId
    ): PatientHasDoctorEntity?

    fun findAllByStatusAndDoctorId(
        status: AccessStatus,
        userId: UserId
    ): List<PatientHasDoctorEntity>

    fun findAllByMedicalCardId(
        medicalCardId: MedicalCardId
    ): List<PatientHasDoctorEntity>

    fun findAllByDoctorId(
        userId: UserId
    ): List<PatientHasDoctorEntity>

    fun existsByMedicalCardIdAndDoctorId(
        medicalCardId: MedicalCardId,
        doctorId: UserId
    ): Boolean

    fun findAllByDoctorIdAndStatus(
        doctorId: UserId,
        status: AccessStatus
    ): List<PatientHasDoctorEntity>

    fun findAllByMedicalCardIdAndStatus(
        medicalCardId: MedicalCardId,
        status: AccessStatus
    ): List<PatientHasDoctorEntity>

}