package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorScheduleId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorScheduleEntity
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WeekDay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DoctorScheduleRepository: JpaRepository<DoctorScheduleEntity, DoctorScheduleId> {

    fun findAllByDoctorId(doctorId: DoctorId): List<DoctorScheduleEntity>

    fun findByDoctorIdAndWeekDay(doctorId: DoctorId, weekDay: WeekDay): DoctorScheduleEntity?

    fun existsByDoctorIdAndWeekDay(doctorId: DoctorId, weekDay: WeekDay): Boolean

    @Modifying
    @Query("DELETE FROM DoctorScheduleEntity d WHERE d.doctor.id = :doctorId")
    fun deleteAllByDoctorId(@Param("doctorId") doctorId: DoctorId)

}