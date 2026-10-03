package com.yehorsk.medicalplatformbackend.common.security

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class AESAttributeConverter : AttributeConverter<String, String> {

    private val ALGORITHM = "AES/GCM/NoPadding"

    override fun convertToDatabaseColumn(attribute: String?): String? {
        // Implementation for converting to database column
        return attribute
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        // Implementation for converting to entity attribute
        return dbData
    }
}