package com.yehorsk.medicalplatformbackend.auth.service.validation

import com.yehorsk.medicalplatformbackend.auth.service.dto.request.RegisterRequestDto
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class RoleMatchesValidator: ConstraintValidator<RoleMatches, RegisterRequestDto> {

    override fun isValid(
        dto: RegisterRequestDto?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (dto == null) return false

        val allowed = setOf("PATIENT", "DOCTOR")

        val valid = dto.role.uppercase() in allowed

        if (!valid) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Role is invalid")
                .addPropertyNode("role")
                .addConstraintViolation()
        }

        return valid
    }

}