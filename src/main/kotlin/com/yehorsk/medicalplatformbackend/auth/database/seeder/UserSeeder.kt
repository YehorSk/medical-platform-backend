package com.yehorsk.medicalplatformbackend.auth.database.seeder

import com.yehorsk.medicalplatformbackend.common.util.PasswordEncoder
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.BloodType
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.Gender
import java.time.LocalDate
import kotlin.random.Random
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(3)
@Component
class UserSeeder(
    private val userRepository: UserRepository,
    private val medicalCardRepository: MedicalCardRepository,
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
        val medicalCard = MedicalCardEntity()
        // assign patient and random medical card data
        userRepository.save(patient)
        medicalCard.patient = patient

        // random age between 18 and 90
        val age = Random.nextInt(18, 91)
        // pick a random day offset within the year to diversify birthdays
        val dayOffset = Random.nextInt(0, 365)
        medicalCard.dateOfBirth = LocalDate.now().minusYears(age.toLong()).minusDays(dayOffset.toLong())
        medicalCard.gender = Gender.entries.toTypedArray()[Random.nextInt(Gender.entries.size)]
        medicalCard.bloodType = BloodType.entries.toTypedArray()[Random.nextInt(BloodType.entries.size)]

        medicalCardRepository.save(medicalCard)

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