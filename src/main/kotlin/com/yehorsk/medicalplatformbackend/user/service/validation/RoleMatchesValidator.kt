package com.yehorsk.medicalplatformbackend.user.service.validation

import com.yehorsk.medicalplatformbackend.user.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.user.service.dto.request.RegisterRequestDto
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class RoleMatchesValidator: ConstraintValidator<RoleMatches, RegisterRequestDto> {

    override fun isValid(
        dto: RegisterRequestDto?,
        context: ConstraintValidatorContext
    ): Boolean {

        if (dto == null) return false
        val valid = dto.role == UserRole.PATIENT || dto.role == UserRole.DOCTOR
        return valid
    }

}