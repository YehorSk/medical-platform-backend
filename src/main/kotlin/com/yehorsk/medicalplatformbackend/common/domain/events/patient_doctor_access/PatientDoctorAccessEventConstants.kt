package com.yehorsk.medicalplatformbackend.common.domain.events.patient_doctor_access

object PatientDoctorAccessEventConstants {

    const val PATIENT_DOCTOR_ACCESS_EXCHANGE = "patient-doctor-access"

    const val ACCESS_REQUESTED_KEY = "patient-doctor-access.requested"
    const val ACCESS_GRANTED_KEY = "patient-doctor-access.granted"
    const val ACCESS_APPROVED_KEY = "patient-doctor-access.approved"
    const val ACCESS_REJECTED_KEY = "patient-doctor-access.rejected"
    const val ACCESS_REVOKED_KEY = "patient-doctor-access.revoked"
}