package com.yehorsk.medicalplatformbackend.doctor.database.seeder

import com.yehorsk.medicalplatformbackend.doctor.database.entity.WorkplaceEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.WorkplaceRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.ClinicRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(5)
@Component
class WorkplaceSeeder(
    private val workplaceRepository: WorkplaceRepository,
    private val clinicRepository: ClinicRepository,
    private val doctorRepository: DoctorRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (workplaceRepository.count() > 0) {
            return
        }

        val doctor = doctorRepository.findByLicenseNumberStartingWith("LIC-001-") ?: return

        val clinic = clinicRepository.findAll().firstOrNull() ?: return

        workplaceRepository.save(
            WorkplaceEntity(
                roomNumber = "101",
                clinic = clinic,
                doctor = doctor
            )
        )
    }
}



