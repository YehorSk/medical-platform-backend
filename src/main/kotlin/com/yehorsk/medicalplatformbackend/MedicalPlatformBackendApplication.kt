package com.yehorsk.medicalplatformbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
class MedicalPlatformBackendApplication

fun main(args: Array<String>) {
    runApplication<MedicalPlatformBackendApplication>(*args)
}
