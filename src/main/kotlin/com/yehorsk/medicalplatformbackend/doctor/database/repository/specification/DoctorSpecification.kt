package com.yehorsk.medicalplatformbackend.doctor.database.repository.specification

import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.SpecializationId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.ClinicEntity
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import com.yehorsk.medicalplatformbackend.doctor.database.entity.SpecializationEntity
import com.yehorsk.medicalplatformbackend.doctor.database.entity.WorkplaceEntity
import com.yehorsk.medicalplatformbackend.doctor.service.dto.request.GetDoctorsWithFilterDto
import com.yehorsk.medicalplatformbackend.medical_card.database.entity.MedicalCardEntity
import com.yehorsk.medicalplatformbackend.patient_doctor_access.database.entity.PatientHasDoctorEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

object DoctorSpecification {

    fun isApproved(): Specification<DoctorEntity> =
        Specification { root, _, cb ->
            cb.isTrue(root.get("approved"))
        }

    fun hasSpecialization(specializationIds: List<SpecializationId>?): Specification<DoctorEntity>? {
        if (specializationIds.isNullOrEmpty()) return null
        return Specification { root, _, cb ->
            root.get<SpecializationEntity>("specialization").get<SpecializationId>("id").`in`(specializationIds)
        }
    }

    fun nameContains(name: String?): Specification<DoctorEntity>? {
        if (name.isNullOrBlank()) return null
        return Specification { root, _, cb ->
            val user = root.get<UserEntity>("user")
            val fullName = cb.concat(
                cb.concat(cb.lower(user.get("firstName")), " "),
                cb.lower(user.get("lastName"))
            )
            cb.like(fullName, "%${name.lowercase().trim()}%")
        }
    }

    fun inCity(city: String?): Specification<DoctorEntity>? {
        if (city.isNullOrBlank()) return null
        return Specification { root, _, cb ->
            val workplace = root.get<WorkplaceEntity>("workplace")
            val clinic = workplace.get<ClinicEntity>("clinic")
            cb.equal(cb.lower(clinic.get("city")), city.lowercase())
        }
    }

    fun hasPatient(patientId: UserId?): Specification<DoctorEntity>? {
        if (patientId == null) return null
        return Specification { root, query, cb ->
            val subquery = query.subquery(PatientHasDoctorId::class.java)
            val subRoot = subquery.from(PatientHasDoctorEntity::class.java)

            subquery.select(subRoot.get("id"))
            subquery.where(
                cb.equal(subRoot.get<UserEntity>("doctor").get<UserId>("id"), root.get<UserEntity>("user").get<UserId>("id")),
                cb.equal(subRoot.get<MedicalCardEntity>("medicalCard").get<UserEntity>("user").get<UserId>("id"), patientId)
            )

            cb.exists(subquery)
        }
    }

    fun buildDynamicSpecification(request: GetDoctorsWithFilterDto, patientId: UserId?): Specification<DoctorEntity> {
        val specifications = mutableListOf<Specification<DoctorEntity>>()

        request.specializations?.let { specializationIds ->
            hasSpecialization(specializationIds)?.let { specifications.add(it) }
        }

        request.search?.let { name ->
            nameContains(name)?.let { specifications.add(it) }
        }

        request.city?.let { city ->
            inCity(city)?.let { specifications.add(it) }
        }

        if(request.getPatientDoctors) {
            hasPatient(patientId)?.let { specifications.add(it) }
        }

        return if(specifications.isEmpty()) {
            Specification { _, _, _ -> null }
        }else{
            Specification.allOf(specifications)
        }
    }

}