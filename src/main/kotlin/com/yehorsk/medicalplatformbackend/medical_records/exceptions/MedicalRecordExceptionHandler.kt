package com.yehorsk.medicalplatformbackend.medical_records.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.domain.response.ErrorResponse
import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.AttachmentNotAvailableException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.AttachmentNotFoundException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.InvalidAttachmentException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordAccessDeniedException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordAlreadyExistsException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.MedicalRecordNotFoundException
import com.yehorsk.medicalplatformbackend.medical_records.exceptions.types.UnsupportedAttachmentTypeException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class MedicalRecordExceptionHandler {

    @ExceptionHandler(MedicalRecordNotFoundException::class)
    fun onMedicalRecordNotFound(e: MedicalRecordNotFoundException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(MedicalRecordAlreadyExistsException::class)
    fun onMedicalRecordAlreadyExists(e: MedicalRecordAlreadyExistsException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(MedicalRecordAccessDeniedException::class)
    fun onMedicalRecordAccessDenied(e: MedicalRecordAccessDeniedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(AttachmentNotFoundException::class)
    fun onAttachmentNotFound(e: AttachmentNotFoundException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(AttachmentNotAvailableException::class)
    fun onAttachmentNotAvailable(e: AttachmentNotAvailableException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(InvalidAttachmentException::class)
    fun onInvalidAttachment(e: InvalidAttachmentException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(UnsupportedAttachmentTypeException::class)
    fun onUnsupportedAttachmentType(e: UnsupportedAttachmentTypeException, request: HttpServletRequest) =
        e.toResponse(request)

    // Thrown by Spring before the controller runs, when spring.servlet.multipart limits are exceeded
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun onMaxUploadSizeExceeded(e: MaxUploadSizeExceededException, request: HttpServletRequest): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(
            ErrorResponse(
                status = HttpStatus.CONTENT_TOO_LARGE.value(),
                errorCode = "FILE_TOO_LARGE",
                message = "Uploaded file is too large",
                path = request.requestURI,
                errors = emptyList()
            )
        )
}
