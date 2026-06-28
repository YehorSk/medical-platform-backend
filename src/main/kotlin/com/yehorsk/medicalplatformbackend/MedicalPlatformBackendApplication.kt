package com.yehorsk.medicalplatformbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
class MedicalPlatformBackendApplication

fun main(args: Array<String>) {
    runApplication<MedicalPlatformBackendApplication>(*args)
}
