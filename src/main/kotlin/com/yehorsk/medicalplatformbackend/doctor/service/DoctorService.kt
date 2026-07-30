package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.auth.service.dto.response.PagedResponseDto
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorResponseDto
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.specification.DoctorSpecification
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.*
import com.yehorsk.medicalplatformbackend.doctor.mappers.toDoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.mappers.toPatientHasDoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.ChangeDoctorApprovalStatusDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.GetDoctorsWithFilterDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorDetailsResponseDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.repository.PatientHasDoctorRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class DoctorService(
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
    private val patientHasDoctorRepository: PatientHasDoctorRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getDoctor(doctorId: DoctorId): DoctorDetailsResponseDto {
        val doctor = doctorRepository.findDoctorEntityById(doctorId)
            ?: throw DoctorDoesNotExistException()
        val patientId = currentUserProvider.getCurrentUserId()
        val patientHasDoctor = doctor.user?.let {
            patientHasDoctorRepository.getActiveRelation(patientId, it.id!!)
        }

        return DoctorDetailsResponseDto(
            doctor = doctor.toDoctorResponseDto(),
            access = patientHasDoctor?.toPatientHasDoctorResponseDto()
        )
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun getAllDoctors(request: GetDoctorsWithFilterDto, pageable: Pageable): PagedResponseDto<DoctorResponseDto> {
        val patientId = if(request.getPatientDoctors) currentUserProvider.getCurrentUserId() else null
        val specs = DoctorSpecification.buildDynamicSpecification(request, patientId)
        val pagedResponse = doctorRepository.findAll(specs)

        return PagedResponseDto(
                content = pagedResponse.map { it.toDoctorResponseDto() },
                page = 0,
                size = pagedResponse.size,
                hasNext = false
            )
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun setSpecialization(specializationId: SpecializationId){
        val specialization = specializationRepository.findSpecializationEntitiesById(specializationId)
            ?: throw SpecializationDoesNotExist()

        val doctor = currentUserProvider.getCurrentUserEntity().doctor
            ?: throw DoctorDoesNotExistException()

        doctor.specialization = specialization
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun removeSpecialization(){
        val doctor = currentUserProvider.getCurrentUserEntity().doctor
            ?: throw DoctorDoesNotExistException()

//        doctor.setSpecialization(null)
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun changeDoctorApprovalStatus(request: ChangeDoctorApprovalStatusDto){
        val doctor = doctorRepository.findDoctorEntityById(request.doctorId)
            ?: throw DoctorDoesNotExistException()

        val user = currentUserProvider.getCurrentUserEntity()

        doctor.changeApprovalStatus(user, request.status)
    }

}