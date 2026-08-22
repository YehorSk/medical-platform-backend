package com.yehorsk.medicalplatformbackend.common.domain.events.patient_doctor_access

import com.yehorsk.medicalplatformbackend.common.domain.events.MedConnectEvent
import com.yehorsk.medicalplatformbackend.common.domain.type.PatientHasDoctorId
import com.yehorsk.medicalplatformbackend.common.domain.type.UserId
import java.time.Instant
import java.util.UUID

sealed class PatientDoctorAccessEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String =
        PatientDoctorAccessEventConstants.PATIENT_DOCTOR_ACCESS_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
) : MedConnectEvent {

    data class Requested(
        val relationId: PatientHasDoctorId,

        val patientId: UserId,
        val patientEmail: String,
        val patientUsername: String,

        val doctorId: UserId,
        val doctorEmail: String,
        val doctorUsername: String,

        val requestedBy: UserId,

        override val eventKey: String =
            PatientDoctorAccessEventConstants.ACCESS_REQUESTED_KEY
    ) : PatientDoctorAccessEvent(), MedConnectEvent

    data class Approved(
        val relationId: PatientHasDoctorId,

        val patientId: UserId,
        val patientEmail: String,
        val patientUsername: String,

        val doctorId: UserId,
        val doctorEmail: String,
        val doctorUsername: String,

        override val eventKey: String =
            PatientDoctorAccessEventConstants.ACCESS_APPROVED_KEY
    ) : PatientDoctorAccessEvent(), MedConnectEvent

    data class Rejected(
        val relationId: PatientHasDoctorId,

        val patientId: UserId,
        val patientEmail: String,
        val patientUsername: String,

        val doctorId: UserId,
        val doctorEmail: String,
        val doctorUsername: String,

        override val eventKey: String =
            PatientDoctorAccessEventConstants.ACCESS_REJECTED_KEY
    ) : PatientDoctorAccessEvent(), MedConnectEvent

    data class Revoked(
        val relationId: PatientHasDoctorId,

        val patientId: UserId,
        val patientEmail: String,
        val patientUsername: String,

        val doctorId: UserId,
        val doctorEmail: String,
        val doctorUsername: String,

        val revokedBy: UserId,
        val notifyEmail: String,

        override val eventKey: String =
            PatientDoctorAccessEventConstants.ACCESS_REVOKED_KEY
    ) : PatientDoctorAccessEvent(), MedConnectEvent
}