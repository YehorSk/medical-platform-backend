package com.yehorsk.medicalplatformbackend.common.infra

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class S3ClientConfig(
    private val props: S3ConfigurationProperties
) {

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(
            props.accessKey,
            props.secretKey
        )

        return S3Client.builder()
            .endpointOverride(URI.create(props.endpoint))
            .region(Region.of(props.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(credentials)
            )
            .serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }

}