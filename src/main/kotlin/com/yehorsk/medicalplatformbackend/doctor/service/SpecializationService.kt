package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponse
import com.yehorsk.medicalplatformbackend.common.service.dto.ApiResponseWithData
import com.yehorsk.medicalplatformbackend.doctor.database.entity.SpecializationEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import com.yehorsk.medicalplatformbackend.doctor.exceptions.types.SpecializationDoesNotExist
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.CreateSpecializationRequestDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.SpecializationResponseDto
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class SpecializationService(
    private val specializationRepository: SpecializationRepository
) {

    @PreAuthorize("isAuthenticated()")
    fun getAll(): ApiResponseWithData<List<SpecializationResponseDto>> {
        val list = specializationRepository.findAll().map { SpecializationResponseDto(it.id, it.name) }
        return ApiResponseWithData(data = list)
    }

    @PreAuthorize("isAuthenticated()")
    fun getById(id: SpecializationId): ApiResponseWithData<SpecializationResponseDto> {
        val specialization = specializationRepository.findSpecializationEntitiesById(id)
            ?: throw SpecializationDoesNotExist()
        return ApiResponseWithData(data = SpecializationResponseDto(specialization.id, specialization.name))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    fun create(request: CreateSpecializationRequestDto): ApiResponseWithData<SpecializationResponseDto> {
        val entity = SpecializationEntity(name = request.name)
        val saved = specializationRepository.save(entity)
        return ApiResponseWithData(data = SpecializationResponseDto(saved.id, saved.name), message = "Specialization created successfully")
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    fun update(id: SpecializationId, request: CreateSpecializationRequestDto): ApiResponseWithData<SpecializationResponseDto> {
        val specialization = specializationRepository.findSpecializationEntitiesById(id)
            ?: throw SpecializationDoesNotExist()

        specialization.name = request.name

        val saved = specializationRepository.save(specialization)

        return ApiResponseWithData(data = SpecializationResponseDto(saved.id, saved.name), message = "Specialization updated successfully")
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    fun delete(id: SpecializationId): ApiResponse {
        val specialization = specializationRepository.findSpecializationEntitiesById(id)
            ?: throw SpecializationDoesNotExist()

        specializationRepository.delete(specialization)

        return ApiResponse(message = "Specialization deleted successfully")
    }

}


