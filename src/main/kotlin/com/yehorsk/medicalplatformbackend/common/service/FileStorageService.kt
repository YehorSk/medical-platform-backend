package com.yehorsk.medicalplatformbackend.common.service

import com.yehorsk.medicalplatformbackend.common.infra.S3ConfigurationProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.util.UUID

@Service
class FileStorageService(
    private val s3Client: S3Client,
    private val props: S3ConfigurationProperties
) {

    private val logger = LoggerFactory.getLogger(FileStorageService::class.java)

    private val quarantineBucket = props.buckets.quarantine
    private val filesBucket = props.buckets.medicalFiles

    fun uploadToQuarantine(fileId: UUID, file: MultipartFile) {
        file.inputStream.use { put(quarantineBucket, quarantineKey(fileId), it, file.size, file.contentType) }
    }

    fun promote(fileId: UUID) {
        s3Client.copyObject(
            CopyObjectRequest.builder()
                .sourceBucket(quarantineBucket).sourceKey(quarantineKey(fileId))
                .destinationBucket(filesBucket).destinationKey(filesKey(fileId))
                .build()
        )
        deleteFromQuarantine(fileId)
    }

    fun openQuarantine(fileId: UUID): InputStream =
        s3Client.getObject(
            GetObjectRequest.builder().bucket(quarantineBucket).key(quarantineKey(fileId)).build()
        )

    fun download(fileId: UUID): InputStream =
        s3Client.getObject(
            GetObjectRequest.builder().bucket(filesBucket).key(filesKey(fileId)).build()
        )

    fun deleteFromQuarantine(fileId: UUID) = delete(quarantineBucket, quarantineKey(fileId))

    fun deleteFromMedicalFiles(fileId: UUID) = delete(filesBucket, filesKey(fileId))

    private fun put(bucket: String, key: String, input: InputStream, size: Long, contentType: String?) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType ?: "application/octet-stream")
                .build(),
            RequestBody.fromInputStream(input, size)
        )
    }

    private fun delete(bucket: String, key: String) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build())
    }

    private fun quarantineKey(id: UUID) = id.toString()
    private fun filesKey(id: UUID) = "attachments/$id"

}