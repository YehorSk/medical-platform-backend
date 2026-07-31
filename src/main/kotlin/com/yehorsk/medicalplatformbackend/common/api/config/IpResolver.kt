package com.yehorsk.medicalplatformbackend.common.api.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class IpResolver {

    fun getClientIp(request: HttpServletRequest): String {
        return request.remoteAddr
    }
}