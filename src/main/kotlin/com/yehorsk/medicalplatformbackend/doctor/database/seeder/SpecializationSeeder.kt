package com.yehorsk.medicalplatformbackend.doctor.database.seeder

import com.yehorsk.medicalplatformbackend.doctor.database.entity.SpecializationEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class SpecializationSeeder(
    private val specializationRepository: SpecializationRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (specializationRepository.count() > 0) {
            return
        }

        specializationRepository.saveAll(
            listOf(
                SpecializationEntity(name = "Cardiology"),
                SpecializationEntity(name = "Dermatology"),
                SpecializationEntity(name = "Neurology"),
                SpecializationEntity(name = "Oncology"),
                SpecializationEntity(name = "Orthopedics"),
                SpecializationEntity(name = "Pediatrics"),
                SpecializationEntity(name = "Psychiatry"),
                SpecializationEntity(name = "Radiology"),
                SpecializationEntity(name = "Surgery"),
                SpecializationEntity(name = "Urology")
            )
        )
    }
}