package com.yehorsk.medicalplatformbackend.common.domain.domain.response

import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val errorCode: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val path: String,
    val errors: List<FieldError>? = null
) {
    data class FieldError(
        val field: String,
        val message: String
    )
}