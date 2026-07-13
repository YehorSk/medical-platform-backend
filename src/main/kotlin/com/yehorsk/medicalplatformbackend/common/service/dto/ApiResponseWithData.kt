package com.yehorsk.medicalplatformbackend.common.service.dto

data class ApiResponseWithData<T>(
    val data: T,
    val message: String = ""
)

