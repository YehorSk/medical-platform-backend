package com.yehorsk.medicalplatformbackend.common.infra

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("minio")
data class S3ConfigurationProperties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val region: String = "us-east-1",
    val buckets: Buckets
) {
    data class Buckets(
        val quarantine: String,
        val medicalFiles: String
    )
}