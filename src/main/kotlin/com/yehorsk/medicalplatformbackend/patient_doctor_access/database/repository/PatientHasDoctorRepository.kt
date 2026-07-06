package com.yehorsk.medicalplatformbackend.patient_doctor_access.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PatientHasDoctorRepository: JpaRepository<PatientHasDoctorEntity, PatientHasDoctorId> {

    fun findByPatientIdAndDoctorId(
        patientId: UserId,
        doctorId: UserId
    ): PatientHasDoctorEntity?

    @Query("""
    SELECT COUNT(phd) > 0
    FROM PatientHasDoctorEntity phd
    WHERE phd.patient.id = :patientId
      AND phd.doctor.id = :doctorId
      AND phd.status IN ('PENDING', 'APPROVED')
    """)
    fun existsActiveRelation(
        patientId: UserId,
        doctorId: UserId
    ): Boolean

    fun findPatientHasDoctorEntityById(
        patientHasDoctorId: PatientHasDoctorId
    ): PatientHasDoctorEntity?

    fun findAllByStatusAndDoctorId(
        status: AccessStatus,
        userId: UserId
    ): List<PatientHasDoctorEntity>

    fun findAllByPatientId(
        userId: UserId
    ): List<PatientHasDoctorEntity>

    fun findAllByDoctorId(
        userId: UserId
    ): List<PatientHasDoctorEntity>

    fun existsByPatientIdAndDoctorId(
        patientId: UserId,
        doctorId: UserId
    ): Boolean

    fun findAllByDoctorIdAndStatus(
        doctorId: UserId,
        status: AccessStatus
    ): List<PatientHasDoctorEntity>

    fun findAllByPatientIdAndStatus(
        patientId: UserId,
        status: AccessStatus
    ): List<PatientHasDoctorEntity>

}