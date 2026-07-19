package com.yehorsk.medicalplatformbackend.auth.service.dto.response

data class PagedResponseDto<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
    val totalElements: Long? = null,
    val totalPages: Int? = null
)