package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DoctorRepository: JpaRepository<DoctorEntity, DoctorId> {

    fun findDoctorEntityBy(doctorId: DoctorId): DoctorEntity?

    fun existsByLicenseNumber(number: String): Boolean

    fun findByLicenseNumber(number: String): DoctorEntity?

    fun findAllByApprovedTrue(): List<DoctorEntity>

    fun findAllByApprovedFalse(): List<DoctorEntity>

    @Query("SELECT d FROM DoctorEntity d WHERE d.approved = true " +
           "AND EXISTS (SELECT 1 FROM d.specializations s WHERE s.specialization.id = :specializationId)")
    fun findApprovedBySpecializationId(@Param("specializationId") specializationId: SpecializationId): List<DoctorEntity>

    @Query(
        "SELECT d FROM DoctorEntity d WHERE " +
        "(:approved IS NULL OR d.approved = :approved) AND " +
        "(:specializationId IS NULL OR EXISTS (SELECT 1 FROM d.specializations s WHERE s.specialization.id = :specializationId)) AND " +
        "(:firstName IS NULL OR LOWER(d.user.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
        "(:lastName IS NULL OR LOWER(d.user.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))"
    )
    fun findByFilters(
        @Param("specializationId") specializationId: SpecializationId?,
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?,
        @Param("approved") approved: Boolean?
    ): List<DoctorEntity>

}