package com.yehorsk.medicalplatformbackend.common.api.config

import org.springframework.http.MediaType
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import tools.jackson.databind.json.JsonMapper

/**
 * Reads JSON from multipart parts sent without a Content-Type.
 * Spring treats such parts as application/octet-stream, which happens with Postman form-data text fields
 * and many form libraries, e.g. the "data" part of POST /api/medical-records.
 * Read-only: responses are never written as octet-stream JSON.
 */
class OctetStreamJsonPartConverter(jsonMapper: JsonMapper) : JacksonJsonHttpMessageConverter(jsonMapper) {

    init {
        supportedMediaTypes = listOf(MediaType.APPLICATION_OCTET_STREAM)
    }

    override fun canWrite(clazz: Class<*>, mediaType: MediaType?): Boolean = false
}
