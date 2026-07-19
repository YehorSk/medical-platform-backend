package com.yehorsk.medicalplatformbackend.patient_doctor_access.service

import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.common.exceptions.types.AccessDeniedException
import com.yehorsk.medicalplatformbackend.common.security.CurrentUserProvider
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.AccessStatus
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.repository.PatientHasDoctorRepository
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.AccessAlreadyApprovedException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.AccessRequestAlreadyExistsException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.DoctorNotFoundException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.PatientNotFoundException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.exceptions.types.RelationDoesNotExistException
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.dto.response.PatientHasDoctorResponse
import com.yehorsk.medicalplatformbackend.patient_doctor_access.service.mappers.toPatientHasDoctorResponse
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.auth.database.repository.UserRepository
import com.yehorsk.medicalplatformbackend.medical_card.database.repository.MedicalCardRepository
import com.yehorsk.medicalplatformbackend.user.service.UserService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class PatientHasDoctorService(
    private val repository: PatientHasDoctorRepository,
    private val medicalCardRepository: MedicalCardRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun patientRequestDoctor(doctorId: UserId) {
        val patientId = currentUserProvider.getCurrentUserId()
        logger.debug("*** patientRequestDoctor => patientId: {}, doctorId: {}", patientId, doctorId)
        createAccessRequest(patientId, doctorId)
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun doctorRequestPatient(patientId: UserId) {
        val doctorId = currentUserProvider.getCurrentUserId()
        logger.debug("*** doctorRequestPatient => patientId: {}, doctorId: {}", patientId, doctorId)
        createAccessRequest(patientId, doctorId)
    }

    private fun createAccessRequest(
        patientId: UserId,
        doctorId: UserId
    ) {
        val medicalCard = medicalCardRepository.findMedicalCardEntityByUserId(patientId)
            ?: throw PatientNotFoundException()
        val doctor = userRepository.findUserEntityById(doctorId)
            ?: throw DoctorNotFoundException()

        if (repository.existsActiveRelation(patientId, doctorId)) {
            throw AccessRequestAlreadyExistsException()
        }

        repository.save(
            PatientHasDoctorEntity(
                medicalCard = medicalCard,
                doctor = doctor
            )
        )
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun approveAccess(relationId: PatientHasDoctorId){
        val userId = currentUserProvider.getCurrentUserId()
        val relation = repository.findPatientHasDoctorEntityById(relationId)
            ?: throw RelationDoesNotExistException()

        if(relation.status == AccessStatus.APPROVED) {
            throw AccessAlreadyApprovedException()
        }

        relation.approve()
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun revokeAccess(relationId: PatientHasDoctorId){
        val relation = repository.findPatientHasDoctorEntityById(relationId)
            ?: throw RelationDoesNotExistException()
        val user = currentUserProvider.getCurrentUserEntity()
        when (user.role) {
            UserRole.DOCTOR -> {
                if (relation.doctor.id != user.id) {
                    throw AccessDeniedException()
                }
                relation.revoke()
            }
            UserRole.PATIENT -> {
                if (relation.medicalCard.id != user.id) {
                    throw AccessDeniedException()
                }
                relation.revoke()
            }
            else -> throw AccessDeniedException()
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getPendingRequestsForDoctor(): List<PatientHasDoctorResponse>{
        val doctorId = currentUserProvider.getCurrentUserId()

        return repository.findAllByDoctorIdAndStatus(
            doctorId = doctorId,
            status = AccessStatus.PENDING
        ).map { it.toPatientHasDoctorResponse() }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    fun getAllRequests(): List<PatientHasDoctorResponse>{
        val user = currentUserProvider.getCurrentUserEntity()
        return when (user.role) {
            UserRole.DOCTOR -> {
                repository.findAllByDoctorId(user.id!!)
            }
            UserRole.PATIENT -> {
                repository.findAllByMedicalCardId(user.id!!)
            }
            else -> throw AccessDeniedException()
        }.map { it.toPatientHasDoctorResponse() }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    fun getMyDoctors(): List<PatientHasDoctorResponse>{
        val patientId = currentUserProvider.getCurrentUserId()
        return repository.findAllByMedicalCardIdAndStatus(
            medicalCardId = patientId,
            status = AccessStatus.APPROVED
        ).map { it.toPatientHasDoctorResponse() }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    fun getMyPatients(): List<PatientHasDoctorResponse>{
        val doctorId = currentUserProvider.getCurrentUserId()
        return repository.findAllByDoctorIdAndStatus(
            doctorId = doctorId,
            status = AccessStatus.APPROVED
        ).map { it.toPatientHasDoctorResponse() }
    }

}