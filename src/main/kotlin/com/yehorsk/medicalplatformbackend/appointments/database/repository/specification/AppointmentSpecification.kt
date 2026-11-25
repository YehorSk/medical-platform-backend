package com.yehorsk.medicalplatformbackend.appointments.database.repository.specification

import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentEntity
import com.yehorsk.medicalplatformbackend.appointments.database.model.AppointmentStatus
import com.yehorsk.medicalplatformbackend.appointments.service.dto.request.GetAppointmentsWithFilterDto
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserEntity
import com.yehorsk.medicalplatformbackend.auth.database.entity.UserRole
import com.yehorsk.medicalplatformbackend.common.domain.type.DoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import com.yehorsk.medicalplatformbackend.doctor.database.entity.DoctorEntity
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

object AppointmentSpecification {

    fun ownedBy(currentUser: UserEntity): Specification<AppointmentEntity>? {
        val userId = currentUser.id!!
        return when (currentUser.role) {
            UserRole.DOCTOR -> Specification { root, _, cb ->
                cb.equal(root.get<UserEntity>("doctor").get<UserId>("id"), userId)
            }
            UserRole.PATIENT -> Specification { root, _, cb ->
                cb.equal(root.get<UserEntity>("patient").get<UserId>("id"), userId)
            }
            UserRole.ADMIN -> null
        }
    }

    fun hasStatuses(statuses: List<AppointmentStatus>?): Specification<AppointmentEntity>? {
        if (statuses.isNullOrEmpty()) return null
        return Specification { root, _, _ ->
            root.get<AppointmentStatus>("status").`in`(statuses)
        }
    }

    fun hasDoctor(doctorId: DoctorId?): Specification<AppointmentEntity>? {
        if (doctorId == null) return null
        return Specification { root, _, cb ->
            cb.equal(
                root.get<UserEntity>("doctor").get<DoctorEntity>("doctor").get<DoctorId>("id"),
                doctorId
            )
        }
    }

    fun hasPatient(patientId: UserId?): Specification<AppointmentEntity>? {
        if (patientId == null) return null
        return Specification { root, _, cb ->
            cb.equal(root.get<UserEntity>("patient").get<UserId>("id"), patientId)
        }
    }

    fun dateTimeFrom(from: Instant?): Specification<AppointmentEntity>? {
        if (from == null) return null
        return Specification { root, _, cb ->
            cb.greaterThanOrEqualTo(root.get("dateTime"), from)
        }
    }

    fun dateTimeTo(to: Instant?): Specification<AppointmentEntity>? {
        if (to == null) return null
        return Specification { root, _, cb ->
            cb.lessThanOrEqualTo(root.get("dateTime"), to)
        }
    }

    fun isUpcoming(upcoming: Boolean, now: Instant): Specification<AppointmentEntity>? {
        if (!upcoming) return null
        return Specification { root, _, cb ->
            cb.greaterThan(root.get("dateTime"), now)
        }
    }

    fun buildDynamicSpecification(
        request: GetAppointmentsWithFilterDto,
        currentUser: UserEntity,
        now: Instant = Instant.now()
    ): Specification<AppointmentEntity> {
        val specifications = listOfNotNull(
            ownedBy(currentUser),
            hasStatuses(request.statuses),
            hasDoctor(request.doctorId),
            hasPatient(request.patientId),
            dateTimeFrom(request.from),
            dateTimeTo(request.to),
            isUpcoming(request.upcoming, now)
        )

        return if (specifications.isEmpty()) {
            Specification { _, _, _ -> null }
        } else {
            Specification.allOf(specifications)
        }
    }
}
