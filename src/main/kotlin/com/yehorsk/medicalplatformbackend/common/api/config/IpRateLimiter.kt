package com.yehorsk.medicalplatformbackend.common.api.config

import com.yehorsk.medicalplatformbackend.common.exceptions.types.RateLimitException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class IpRateLimiter(
    private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private const val IP_RATE_LIMIT_PREFIX = "rate_limit:ip"
    }

    @Value("classpath:scripts/ip_rate_limiter.lua")
    lateinit var rateLimitResource: Resource

    private val rateLimitScript by lazy {
        val script = rateLimitResource.inputStream.use {
            it.readBytes().decodeToString()
        }
        @Suppress("UNCHECKED_CAST")
        DefaultRedisScript(script, List::class.java as Class<List<Long>>)
    }

    fun <T> withIpRateLimit(
        ipAddress: String,
        endpointKey: String,
        resetsIn: Duration,
        maxRequestsPerIp: Int,
        action: () -> T
    ): T {
        val key = "$IP_RATE_LIMIT_PREFIX:$ipAddress:$endpointKey"
        val result = redisTemplate.execute(
            rateLimitScript,
            listOf(key),
            maxRequestsPerIp.toString(),
            resetsIn.seconds.toString()
        )
        val currentCount = result[0]
        val ttl = result[1]
        val allowed = result[2] == 1L

        return if (allowed) {
            action()
        } else {
            throw RateLimitException(resetsInSeconds = ttl)
        }
    }
}