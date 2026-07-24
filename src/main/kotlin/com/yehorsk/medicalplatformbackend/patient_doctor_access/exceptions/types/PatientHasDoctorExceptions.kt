package com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types

import com.yehorsk.medicalplatformbackend.common.domain.exceptions.AppException
import org.springframework.http.HttpStatus

class AccessRequestAlreadyExistsException : AppException("RELATION_ALREADY_EXISTS", HttpStatus.CONFLICT, "Relation already exists")
class AccessAlreadyApprovedException : AppException("RELATION_ALREADY_APPROVED", HttpStatus.CONFLICT, "Relation already approved")
class AccessAlreadyRejectedException : AppException("RELATION_ALREADY_REJECTED", HttpStatus.CONFLICT, "Relation already rejected")
class RelationDoesNotExistException : AppException("RELATION_NOT_EXIST", HttpStatus.NOT_FOUND, "Relation does not exist")
class PatientNotFoundException : AppException("PATIENT_NOT_FOUND", HttpStatus.NOT_FOUND, "Patient not found")
class DoctorNotFoundException : AppException("DOCTOR_NOT_FOUND", HttpStatus.NOT_FOUND, "Doctor not found")
