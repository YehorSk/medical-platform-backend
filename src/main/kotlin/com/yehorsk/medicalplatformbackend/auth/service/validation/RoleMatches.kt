package com.yehorsk.medicalplatformbackend.auth.service.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [RoleMatchesValidator::class])
annotation class RoleMatches(
    val message: String = "Invalid role",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)