package com.yehorsk.medicalplatformbackend.common.api.config

import org.springframework.http.converter.HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.databind.json.JsonMapper

@Component
class WebMvcConfig(
    private val ipRateLimitInterceptor: IpRateLimitInterceptor,
    private val jsonMapper: JsonMapper,
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(ipRateLimitInterceptor)
            .addPathPatterns("/api/**")
    }

    // Added last, so every built-in converter keeps priority and this only kicks in when nothing else can read the part
    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(OctetStreamJsonPartConverter(jsonMapper))
    }
}
