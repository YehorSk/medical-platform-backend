package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.*
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.ChangeDoctorApprovalStatusDto
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class DoctorService(
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun addSpecialization(specializationId: SpecializationId){
        val specialization = specializationRepository.findSpecializationEntitiesById(specializationId)
            ?: throw SpecializationDoesNotExist()

        val doctor = currentUserProvider.getCurrentUserEntity().doctor
            ?: throw DoctorDoesNotExistException()

        doctor.addSpecialization(specialization)
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun removeSpecialization(specializationId: SpecializationId){
        specializationRepository.findSpecializationEntitiesById(specializationId)
            ?: throw SpecializationDoesNotExist()

        val doctor = currentUserProvider.getCurrentUserEntity().doctor
            ?: throw DoctorDoesNotExistException()

        doctor.removeSpecialization(specializationId)
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun changeDoctorApprovalStatus(request: ChangeDoctorApprovalStatusDto){
        val doctor = doctorRepository.findDoctorEntityBy(request.doctorId)
            ?: throw DoctorDoesNotExistException()

        val user = currentUserProvider.getCurrentUserEntity()

        doctor.changeApprovalStatus(user, request.status)
    }

}