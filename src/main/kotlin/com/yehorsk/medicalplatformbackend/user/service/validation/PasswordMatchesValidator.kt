package com.yehorsk.medicalplatformbackend.user.service.validation

import com.yehorsk.medicalplatformbackend.user.service.dto.request.RegisterRequestDto
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordMatchesValidator: ConstraintValidator<PasswordMatches, RegisterRequestDto> {

    override fun isValid(dto: RegisterRequestDto?, context: ConstraintValidatorContext?): Boolean {
        if (dto == null) return true
        if (dto.password == dto.passwordConfirm) return true

        context?.disableDefaultConstraintViolation()
        context?.buildConstraintViolationWithTemplate("Passwords do not match")
            ?.addPropertyNode("passwordConfirm")
            ?.addConstraintViolation()

        return false
    }

}