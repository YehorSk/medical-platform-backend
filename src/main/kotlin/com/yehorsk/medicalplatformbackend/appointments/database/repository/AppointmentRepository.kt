package com.yehorsk.medicalplatformbackend.appointments.database.repository

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.common.domain.type.AppointmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface AppointmentRepository : JpaRepository<AppointmentEntity, AppointmentId> {

    fun findAllByDoctorId(doctorId: UserId): List<AppointmentEntity>

    fun findAllByPatientId(patientId: UserId): List<AppointmentEntity>

    fun findAllByStatus(status: AppointmentStatus): List<AppointmentEntity>

    fun existsByDoctorIdAndDateTime(doctorId: UserId, dateTime: Instant): Boolean

    fun findByDoctorIdAndId(doctorId: UserId, appointmentId: AppointmentId): AppointmentEntity?

    fun findAllByDoctorIdAndStatus(doctorId: UserId, status: AppointmentStatus): List<AppointmentEntity>

    fun findAllByPatientIdAndStatus(patientId: UserId, status: AppointmentStatus): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.dateTime >= :startDate
        AND a.dateTime <= :endDate
        ORDER BY a.dateTime ASC
    """)
    fun findAppointmentsByDateRange(
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant
    ): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.doctor.id = :doctorId
        AND a.dateTime >= :startDate
        AND a.dateTime <= :endDate
        ORDER BY a.dateTime ASC
    """)
    fun findAppointmentsByDoctorAndDateRange(
        @Param("doctorId") doctorId: UserId,
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant
    ): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.patient.id = :patientId
        AND a.dateTime >= :startDate
        AND a.dateTime <= :endDate
        ORDER BY a.dateTime ASC
    """)
    fun findAppointmentsByPatientAndDateRange(
        @Param("patientId") patientId: UserId,
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant
    ): List<AppointmentEntity>

    fun findByDoctorIdAndPatientIdAndDateTimeAndStatus(
        doctorId: UserId,
        patientId: UserId,
        dateTime: Instant,
        status: AppointmentStatus
    ): AppointmentEntity?

    @Query("""
        SELECT COUNT(a) > 0 FROM AppointmentEntity a
        WHERE a.doctor.id = :doctorId
        AND a.patient.id = :patientId
        AND a.dateTime = :dateTime
    """)
    fun existsByDoctorIdAndPatientIdAndDateTime(
        @Param("doctorId") doctorId: UserId,
        @Param("patientId") patientId: UserId,
        @Param("dateTime") dateTime: Instant
    ): Boolean

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.doctor.id = :doctorId
        AND a.status != 'CANCELLED'
        ORDER BY a.dateTime ASC
    """)
    fun findActiveAppointmentsByDoctorId(
        @Param("doctorId") doctorId: UserId
    ): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.patient.id = :patientId
        AND a.status != 'CANCELLED'
        ORDER BY a.dateTime ASC
    """)
    fun findActiveAppointmentsByPatientId(
        @Param("patientId") patientId: UserId
    ): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.doctor.id = :doctorId
        AND a.dateTime > :currentDateTime
        AND a.status != 'CANCELLED'
        ORDER BY a.dateTime ASC
    """)
    fun findUpcomingAppointmentsByDoctorId(
        @Param("doctorId") doctorId: UserId,
        @Param("currentDateTime") currentDateTime: Instant
    ): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE a.patient.id = :patientId
        AND a.dateTime > :currentDateTime
        AND a.status != 'CANCELLED'
        ORDER BY a.dateTime ASC
    """)
    fun findUpcomingAppointmentsByPatientId(
        @Param("patientId") patientId: UserId,
        @Param("currentDateTime") currentDateTime: Instant
    ): List<AppointmentEntity>

    @Query("""
        SELECT a FROM AppointmentEntity a
        WHERE (a.doctor.id = :userId1 AND a.patient.id = :userId2)
        OR (a.doctor.id = :userId2 AND a.patient.id = :userId1)
        ORDER BY a.dateTime ASC
    """)
    fun findAppointmentsBetweenUsers(
        @Param("userId1") userId1: UserId,
        @Param("userId2") userId2: UserId
    ): List<AppointmentEntity>
}

