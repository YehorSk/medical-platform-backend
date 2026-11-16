package com.yehorsk.medicalplatformbackend.medical_records.controller

import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordAttachmentId
import com.yehorsk.medicalplatformbackend.common.domain.type.MedicalRecordId
import com.yehorsk.medicalplatformbackend.medical_records.service.MedicalRecordAttachmentService
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.MedicalRecordService
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.request.CreateMedicalRecordRequestDto
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.AttachmentResponseDto
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.MedicalRecordCreatedResponseDto
import com.yehorsk.medicalplatformbackend.medical_records.service.dto.response.MedicalRecordDetailsResponseDto
import jakarta.validation.Valid
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/medical-records")
class MedicalRecordController(
    private val medicalRecordService: MedicalRecordService,
    private val attachmentService: MedicalRecordAttachmentService,
) {

    // JSON-only: creates a record without files
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @PreAuthorize("hasRole('DOCTOR')")
    fun createMedicalRecord(
        @Valid @RequestBody request: CreateMedicalRecordRequestDto
    ): MedicalRecordCreatedResponseDto {
        return medicalRecordService.createMedicalRecord(request)
    }

    // multipart/form-data: part "data" = record JSON (Content-Type: application/json), parts "files" = attachments
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('DOCTOR')")
    fun createMedicalRecordWithAttachments(
        @Valid @RequestPart("data") request: CreateMedicalRecordRequestDto,
        @RequestPart("files", required = false) files: List<MultipartFile>?
    ): MedicalRecordCreatedResponseDto {
        return medicalRecordService.createMedicalRecord(request, files.orEmpty())
    }

    // Record with every attachment's status, so the doctor can see which files failed and re-upload them
    @GetMapping("/{recordId}")
    fun getMedicalRecord(
        @PathVariable recordId: MedicalRecordId
    ): MedicalRecordDetailsResponseDto {
        return medicalRecordService.getMedicalRecord(recordId)
    }

    // Adds files to an existing record (e.g. re-uploading failed ones); written to the audit log.
    // multipart/form-data with one or more parts named "files"
    @PostMapping("/{recordId}/attachments", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('DOCTOR')")
    fun addAttachments(
        @PathVariable recordId: MedicalRecordId,
        @RequestPart("files") files: List<MultipartFile>
    ): List<AttachmentResponseDto> {
        return attachmentService.addAttachments(recordId, files)
    }

    @GetMapping("/{recordId}/attachments")
    fun getAttachments(
        @PathVariable recordId: MedicalRecordId
    ): List<AttachmentResponseDto> {
        return attachmentService.getAttachments(recordId)
    }

    @GetMapping("/{recordId}/attachments/{attachmentId}")
    fun downloadAttachment(
        @PathVariable recordId: MedicalRecordId,
        @PathVariable attachmentId: MedicalRecordAttachmentId
    ): ResponseEntity<InputStreamResource> {
        val download = attachmentService.downloadAttachment(recordId, attachmentId)
        val disposition = ContentDisposition.attachment()
            .filename(download.filename, StandardCharsets.UTF_8)
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .header("X-Content-Type-Options", "nosniff")
            .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
            .contentType(MediaType.parseMediaType(download.contentType))
            .contentLength(download.sizeBytes)
            .body(InputStreamResource(download.content))
    }
}
