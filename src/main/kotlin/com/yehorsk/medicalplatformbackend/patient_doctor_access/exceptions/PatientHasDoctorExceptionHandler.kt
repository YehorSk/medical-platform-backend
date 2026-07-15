package com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.toResponse
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.AccessAlreadyApprovedException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.AccessRequestAlreadyExistsException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.RelationDoesNotExistException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.DoctorNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class PatientHasDoctorExceptionHandler {

    @ExceptionHandler(AccessRequestAlreadyExistsException::class)
    fun onAccessRequestAlreadyExist(e: AccessRequestAlreadyExistsException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(AccessAlreadyApprovedException::class)
    fun onAccessAlreadyApproved(e: AccessAlreadyApprovedException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(RelationDoesNotExistException::class)
    fun onRelationDoesNotExist(e: RelationDoesNotExistException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(PatientNotFoundException::class)
    fun onPatientNotFound(e: PatientNotFoundException, request: HttpServletRequest) =
        e.toResponse(request)

    @ExceptionHandler(DoctorNotFoundException::class)
    fun onDoctorNotFound(e: DoctorNotFoundException, request: HttpServletRequest) =
        e.toResponse(request)

}