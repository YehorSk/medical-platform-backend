package com.yehorsk.medicalplatformbackend.doctor.database.seeder

import com.yehorsk.medicalplatformbackend.doctor.database.entity.ClinicEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.ClinicRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(4)
@Component
class ClinicSeeder(
    private val clinicRepository: ClinicRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (clinicRepository.count() > 0) {
            return
        }

        clinicRepository.saveAll(
            listOf(
                ClinicEntity(
                    name = "Central Medical Clinic",
                    address = "123 Main Street",
                    city = "New York",
                    phone = "+1-555-0123"
                ),
                ClinicEntity(
                    name = "Downtown Health Center",
                    address = "456 Oak Avenue",
                    city = "New York",
                    phone = "+1-555-0124"
                ),
                ClinicEntity(
                    name = "Riverside Medical Plaza",
                    address = "789 River Road",
                    city = "Los Angeles",
                    phone = "+1-555-0125"
                ),
                ClinicEntity(
                    name = "Northside Wellness Clinic",
                    address = "321 North Boulevard",
                    city = "Chicago",
                    phone = "+1-555-0126"
                )
            )
        )
    }
}

