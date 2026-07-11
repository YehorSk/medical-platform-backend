package com.yehorsk.medicalplatformbackend.auth.database.seeder

import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.database.repository.DoctorRepository
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(3)
@Component
class UserSeeder(
    private val userRepository: UserRepository,
    private val doctorRepository: DoctorRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (userRepository.count() > 0) {
            return
        }

        val commonPassword = "12345678"
        val hashedPassword = passwordEncoder.encode(commonPassword)!!

        // Create patient
        val patient = UserEntity(
            email = "john.doe@example.com",
            hashedPassword = hashedPassword,
            firstName = "John",
            lastName = "Doe",
            role = UserRole.PATIENT,
            hasVerifiedEmail = true
        )
        userRepository.save(patient)

        // Create doctor
        val doctorUser = UserEntity(
            email = "doctor@example.com",
            hashedPassword = hashedPassword,
            firstName = "Jane",
            lastName = "Smith",
            role = UserRole.DOCTOR,
            title = "Dr.",
            hasVerifiedEmail = true
        )
        userRepository.save(doctorUser)

        val doctor = DoctorEntity(
            licenseNumber = "LIC-001-${System.currentTimeMillis()}",
            user = doctorUser,
            approved = true,
            approvedBy = null,
            approvedAt = java.time.Instant.now()
        )
        doctorRepository.save(doctor)

        // Create admin
        val admin = UserEntity(
            email = "admin@example.com",
            hashedPassword = hashedPassword,
            firstName = "Admin",
            lastName = "User",
            role = UserRole.ADMIN,
            hasVerifiedEmail = true
        )
        userRepository.save(admin)
    }
}