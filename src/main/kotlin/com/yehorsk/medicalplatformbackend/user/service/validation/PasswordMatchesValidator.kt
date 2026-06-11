package com.yehorsk.medicalplatformbackend.user.service.validation

import com.yehorsk.medicalplatformbackend.user.service.dto.request.RegisterRequestDto
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordMatchesValidator: ConstraintValidator<PasswordMatches, RegisterRequestDto> {

    override fun isValid(dto: RegisterRequestDto?, p1: ConstraintValidatorContext?): Boolean {
        if (dto == null) return true
        return dto.password == dto.passwordConfirm
    }

}