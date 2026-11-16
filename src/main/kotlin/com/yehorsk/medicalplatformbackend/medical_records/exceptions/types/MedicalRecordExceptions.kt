package com.yehorsk.medicalplatformbackend.medical_records.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class MedicalRecordNotFoundException :
    AppException("MEDICAL_RECORD_NOT_FOUND", HttpStatus.NOT_FOUND, "Medical record not found")

class MedicalRecordAlreadyExistsException :
    AppException("MEDICAL_RECORD_ALREADY_EXISTS", HttpStatus.CONFLICT, "Medical record already exists for this appointment")

class MedicalRecordAccessDeniedException :
    AppException("MEDICAL_RECORD_ACCESS_DENIED", HttpStatus.FORBIDDEN, "You do not have access to this medical record")

class AttachmentNotFoundException :
    AppException("ATTACHMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "Attachment not found")

class AttachmentNotAvailableException(status: String) :
    AppException("ATTACHMENT_NOT_AVAILABLE", HttpStatus.CONFLICT, "Attachment is not available for download (status: $status)")

class InvalidAttachmentException(message: String) :
    AppException("INVALID_ATTACHMENT", HttpStatus.BAD_REQUEST, message)

class UnsupportedAttachmentTypeException(detectedType: String) :
    AppException("UNSUPPORTED_ATTACHMENT_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File type '$detectedType' is not allowed")
