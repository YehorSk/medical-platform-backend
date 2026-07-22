package com.yehorsk.medicalplatformbackend.doctor.mappers

import com.yehorsk.medicalplatformbackend.auth.service.mappers.toUserResponseDto
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.ClinicResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.DoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.PatientHasDoctorResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.SpecializationResponseDto
import com.yehorsk.medicalplatformbackend.doctor.service.dto.response.WorkplaceResponseDto
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity

fun DoctorEntity.toDoctorResponseDto(): DoctorResponseDto {
    return DoctorResponseDto(
        id = this.id,
        licenseNumber = this.licenseNumber,
        createdAt = this.createdAt,
        user = this.user?.toUserResponseDto(),
        approved = this.approved,
        description = this.description,
        specialization = this.specialization?.let { spec ->
            SpecializationResponseDto(
                id = spec.id,
                name = spec.name
            )
        },
        workplace = this.workplace?.let { wp ->
            WorkplaceResponseDto(
                id = wp.id,
                roomNumber = wp.roomNumber,
                clinic = ClinicResponseDto(
                    id = wp.clinic.id,
                    name = wp.clinic.name,
                    address = wp.clinic.address,
                    phone = wp.clinic.phone,
                    city = wp.clinic.city
                )
            )
        },
        updatedAt = this.updatedAt,
        approvedAt = this.approvedAt
    )
}

fun PatientHasDoctorEntity.toPatientHasDoctorResponseDto() = PatientHasDoctorResponseDto(
    id = id!!,
    status = status,
    initiatedBy = initiatedBy,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)