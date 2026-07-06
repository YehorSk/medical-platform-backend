package com.yehorsk.medicalplatformbackend.auth.service.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.full.memberProperties

class PasswordMatchesValidator: ConstraintValidator<PasswordMatches, Any> {

    override fun isValid(dto: Any?, context: ConstraintValidatorContext?): Boolean {
        if (dto == null) return true

        return try {
            val password = dto::class.memberProperties
                .find { it.name == "password" }
                ?.getter?.call(dto) as? String
            val passwordConfirm = dto::class.memberProperties
                .find { it.name == "passwordConfirm" }
                ?.getter?.call(dto) as? String


            if (password == passwordConfirm) {
                true
            } else {
                context?.disableDefaultConstraintViolation()
                context?.buildConstraintViolationWithTemplate("Passwords do not match")
                    ?.addPropertyNode("passwordConfirm")
                    ?.addConstraintViolation()
                false
            }
        } catch (e: Exception) {
            true
        }
    }


}