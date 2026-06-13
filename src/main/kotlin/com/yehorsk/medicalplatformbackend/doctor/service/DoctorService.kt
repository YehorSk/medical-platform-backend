package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorHasSpecializationsEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.SpecializationResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.mappers.toResponseDto
import com.yehorsk.medicalplatformbackend.user.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.DoctorSearchRequest
import com.yehorsk.medicalplatformbackend.user.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorForbiddenException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorNotAuthenticatedException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorNotFoundException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.DoctorUserNotFoundException
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.SpecializationNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class DoctorService(
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: UserRepository
) {

    // Public methods for patients/users

    fun getAllSpecializations(): List<SpecializationResponseDto> {
        return specializationRepository.findAll()
            .map { it.toResponseDto() }
    }

    fun getDoctorsBySpecialization(specializationId: SpecializationId): List<DoctorResponseDto> {
        return getDoctors(specializationId, null, null, true)
    }

    fun getDoctors(searchRequest: DoctorSearchRequest): List<DoctorResponseDto> {
        return getDoctors(
            searchRequest.specializationId,
            searchRequest.firstName,
            searchRequest.lastName,
            searchRequest.approved
        )
    }

    fun getDoctors(
        specializationId: SpecializationId? = null,
        firstName: String? = null,
        lastName: String? = null,
        approved: Boolean? = true
    ): List<DoctorResponseDto> {
        val firstNameParam = firstName?.trim()?.takeIf { it.isNotBlank() }
        val lastNameParam = lastName?.trim()?.takeIf { it.isNotBlank() }
        return doctorRepository.findByFilters(specializationId, firstNameParam, lastNameParam, approved)
            .map { it.toResponseDto() }
    }

    fun getDoctorById(doctorId: DoctorId): DoctorResponseDto {
        val doctor = doctorRepository.findById(doctorId)
            .orElseThrow { DoctorNotFoundException() }

        if (!doctor.approved) {
            throw DoctorNotFoundException()
        }

        return doctor.toResponseDto()
    }

    // Doctor-specific methods

    @PreAuthorize("hasRole('DOCTOR')")
    fun getMyProfile(): DoctorResponseDto {
        val user = currentUserProvider.getCurrentUserEntity()
        val doctor = user.doctor ?: throw DoctorNotFoundException()
        return doctor.toResponseDto()
    }

    // Admin methods

    @PreAuthorize("hasRole('ADMIN')")
    fun getUnapprovedDoctors(): List<DoctorResponseDto> {
        return doctorRepository.findAllByApprovedFalse()
            .map { it.toResponseDto() }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun approveDoctor(doctorId: DoctorId) {
        val doctor = doctorRepository.findById(doctorId)
            .orElseThrow { DoctorNotFoundException() }

        if (doctor.approved) {
            throw IllegalArgumentException("Doctor is already approved")
        }

        val admin = currentUserProvider.getCurrentUserEntity()
        doctor.approve(admin)
        doctorRepository.save(doctor)
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional
    fun addSpecialization(specializationId: SpecializationId) {
        val specialization = specializationRepository.findSpecializationEntitiesById(specializationId)
            ?: throw SpecializationNotFoundException()
        val user = currentUserProvider.getCurrentUserEntity()
        val doctor = user.doctor
            ?: throw DoctorNotFoundException()
        doctor.addSpecialization(specialization)
        doctorRepository.save(doctor)
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional
    fun removeSpecialization(specializationId: SpecializationId) {
        val specialization = specializationRepository.findSpecializationEntitiesById(specializationId)
            ?: throw SpecializationNotFoundException()
        val user = currentUserProvider.getCurrentUserEntity()
        val doctor = user.doctor
            ?: throw DoctorNotFoundException()
        doctor.removeSpecialization(specializationId)
        doctorRepository.save(doctor)
    }

}