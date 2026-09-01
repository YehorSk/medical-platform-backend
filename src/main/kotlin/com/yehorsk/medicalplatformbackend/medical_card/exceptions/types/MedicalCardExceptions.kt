package com.yehorsk.medicalplatformbackend.medical_card.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class InvalidMedicalCardDataException(message: String): AppException("INVALID_MEDICAL_CARD_DATA", HttpStatus.BAD_REQUEST, message)

