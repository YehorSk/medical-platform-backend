package com.yehorsk.medicalplatformbackend.doctor.service

import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import com.yehorsk.medicalplatformbackend.user.database.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class DoctorService(
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: UserRepository
) {



}