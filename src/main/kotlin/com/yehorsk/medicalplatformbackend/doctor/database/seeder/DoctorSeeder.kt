package com.yehorsk.medicalplatformbackend.doctor.database.seeder

import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WorkplaceEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.ClinicRepository
import com.yehorsk.medicalplatformbackend.doctor.database.repository.SpecializationRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.Instant

@Order(7)
@Component
class DoctorSeeder(
    private val userRepository: UserRepository,
    private val doctorRepository: DoctorRepository,
    private val specializationRepository: SpecializationRepository,
    private val clinicRepository: ClinicRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (doctorRepository.count() > 0) {
            return
        }

        val commonPassword = "12345678"
        val hashedPassword = passwordEncoder.encode(commonPassword)!!

        val specializations = specializationRepository.findAll()
        val clinics = clinicRepository.findAll()

        val firstNames = listOf(
            "James", "Mary", "Robert", "Patricia", "John", "Jennifer",
            "Michael", "Linda", "William", "Elizabeth", "David", "Barbara",
            "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah",
            "Charles", "Karen"
        )

        val lastNames = listOf(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia",
            "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez",
            "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore",
            "Jackson", "Martin"
        )

        repeat(20) { index ->
            val firstName = firstNames[index]
            val lastName = lastNames[index]

            val doctorUser = UserEntity(
                email = "doctor${index + 1}@example.com",
                hashedPassword = hashedPassword,
                firstName = firstName,
                lastName = lastName,
                role = UserRole.DOCTOR,
                title = "Dr.",
                hasVerifiedEmail = true
            )
            userRepository.save(doctorUser)

            val doctor = DoctorEntity(
                licenseNumber = "LIC-${(index + 1).toString().padStart(3, '0')}-${System.currentTimeMillis()}",
                user = doctorUser,
                approved = true,
                approvedBy = null,
                approvedAt = Instant.now(),
                description = "Experienced $firstName $lastName providing quality patient care."
            )

            if (specializations.isNotEmpty()) {
                val specialization = specializations[index % specializations.size]
                doctor.specialization = specialization
            }

            if (clinics.isNotEmpty()) {
                val clinic = clinics[index % clinics.size]
                val workplace = WorkplaceEntity(
                    doctor = doctor,
                    clinic = clinic,
                    roomNumber = "Room ${100 + index}"
                )
                doctor.workplace = workplace
            }

            doctorRepository.save(doctor)
        }
    }
}