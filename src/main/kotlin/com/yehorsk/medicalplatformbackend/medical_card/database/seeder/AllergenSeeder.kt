package com.yehorsk.medicalplatformbackend.medical_card.database.seeder

import com.yehorsk.medicalplatformbackend.medical_card.database.entity.AllergenEntity
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.AllergenCategoryRepository
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.AllergenRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(2)
class AllergenSeeder(
    private val allergenRepository: AllergenRepository,
    private val categoryRepository: AllergenCategoryRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {

        if (allergenRepository.count() > 0) return

        val categories = categoryRepository.findAll()
            .associateBy { it.name }

        val allergens = listOf(
            AllergenEntity(name = "Peanuts", category = categories["Food"]!!),
            AllergenEntity(name = "Milk", category = categories["Food"]!!),
            AllergenEntity(name = "Penicillin", category = categories["Drug"]!!),
            AllergenEntity(name = "Pollen", category = categories["Environmental"]!!)
        )

        allergenRepository.saveAll(allergens)
    }
}