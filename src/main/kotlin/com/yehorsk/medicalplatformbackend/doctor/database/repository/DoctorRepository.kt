package com.yehorsk.medicalplatformbackend.doctor.database.repository

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DoctorRepository: JpaRepository<DoctorEntity, DoctorId>, JpaSpecificationExecutor<DoctorEntity>, DoctorRepositoryCustom {

    @EntityGraph(attributePaths = ["user", "specialization", "workplace", "workplace.clinic"])
    fun findDoctorEntityById(doctorId: DoctorId): DoctorEntity?

    fun existsByLicenseNumber(number: String): Boolean

    fun findByLicenseNumberStartingWith(prefix: String): DoctorEntity?

    fun findAllByApprovedTrue(): List<DoctorEntity>

    fun findAllByApprovedFalse(): List<DoctorEntity>

    @Query("SELECT d FROM DoctorEntity d WHERE d.approved = true AND d.specialization.id = :specializationId")
    fun findApprovedBySpecializationId(@Param("specializationId") specializationId: SpecializationId): List<DoctorEntity>

    override fun findAllSliced(spec: Specification<DoctorEntity>, pageable: Pageable): Slice<DoctorEntity>

    @EntityGraph(attributePaths = ["user", "specialization", "workplace", "workplace.clinic"])
    override fun findAll(spec: Specification<DoctorEntity>): List<DoctorEntity>

}