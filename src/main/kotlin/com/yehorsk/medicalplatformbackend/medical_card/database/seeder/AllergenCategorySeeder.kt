package com.yehorsk.medicalplatformbackend.medical_card.database.seeder

import com.yehorsk.medicalplatformbackend.medical_card.database.entity.AllergenCategoryEntity
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.AllergenCategoryRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(1)
@Component
class AllergenCategorySeeder(
    private val repository: AllergenCategoryRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {

        if (repository.count() > 0) return

        repository.saveAll(
            listOf(
                AllergenCategoryEntity(name = "Food"),
                AllergenCategoryEntity(name = "Drug"),
                AllergenCategoryEntity(name = "Environmental"),
                AllergenCategoryEntity(name = "Insect"),
                AllergenCategoryEntity(name = "Contact"),
                AllergenCategoryEntity(name = "Other")
            )
        )
    }
}