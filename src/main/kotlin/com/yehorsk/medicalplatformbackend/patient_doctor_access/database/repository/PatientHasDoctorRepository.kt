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

    @Query("""
    SELECT COUNT(phd) > 0
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.user.id = :patientId
      AND phd.doctor.id = :doctorId
      AND phd.status IN ('PENDING', 'APPROVED')
    """)
    fun existsActiveRelation(
        @Param("patientId") patientId: UserId,
        @Param("doctorId") doctorId: UserId
    ): Boolean

    @Query("""
    SELECT phd
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.user.id = :patientId
      AND phd.status = :status
    """)
    fun findAllRelationWithStatus(
        @Param("patientId") patientId: UserId,
        @Param("status") status: AccessStatus
    ): List<PatientHasDoctorEntity>

    @Query("""
    SELECT phd
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.user.id = :patientId
      AND phd.doctor.id = :doctorId
      AND phd.status IN ('PENDING', 'APPROVED')
    """)
    fun getActiveRelation(
        @Param("patientId") patientId: UserId,
        @Param("doctorId") doctorId: UserId
    ): PatientHasDoctorEntity?

    @Query("""
    SELECT phd
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.user.id = :patientId
      AND phd.doctor.id = :doctorId
    """)
    fun getRelation(
        @Param("patientId") patientId: UserId,
        @Param("doctorId") doctorId: UserId
    ): PatientHasDoctorEntity?

    @Query("""
    SELECT phd
    FROM PatientHasDoctorEntity phd
    WHERE phd.medicalCard.user.id = :patientId
      AND phd.id = :patientHasDoctorId
    """)
    fun findRelationByIdAndPatientId(
        @Param("patientId") patientId: UserId,
        @Param("patientHasDoctorId") patientHasDoctorId: PatientHasDoctorId
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

}