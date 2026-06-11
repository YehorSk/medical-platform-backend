package com.yehorsk.medicalplatformbackend.common.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    fun encode(raw: String) = bcrypt.encode(raw)

    fun matches(raw: String, encoded: String): Boolean = bcrypt.matches(raw, encoded)

}