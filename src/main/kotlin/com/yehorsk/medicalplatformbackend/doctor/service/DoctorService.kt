package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import com.yehorsk.medicalplatformbackend.user.database.repository.UserRepository
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
    private val userRepository: UserRepository
) {

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional
    fun addSpecialization(specializationId: SpecializationId) {

    }

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional
    fun removeSpecialization(specializationId: SpecializationId) {

    }

}