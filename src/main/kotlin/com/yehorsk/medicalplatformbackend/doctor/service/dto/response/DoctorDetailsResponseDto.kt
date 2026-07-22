package com.yehorsk.medicalplatformbackend.doctor.service.dto.response

data class DoctorDetailsResponseDto(
    val doctor: DoctorResponseDto,
    val access: PatientHasDoctorResponseDto? = null
)
