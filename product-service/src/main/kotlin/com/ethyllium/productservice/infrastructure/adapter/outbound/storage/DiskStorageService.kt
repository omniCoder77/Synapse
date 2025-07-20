package com.ethyllium.productservice.infrastructure.adapter.outbound.storage

import com.ethyllium.productservice.domain.port.driven.StorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Component
class DiskStorageService : StorageService {

    private val logger = LoggerFactory.getLogger(DiskStorageService::class.java)

    @Value("\${app.upload.base-dir:./uploads}")
    private lateinit var baseUploadDir: String

    override fun store(file: FilePart, storagePath: String): Mono<String> {
        val uploadDir = Paths.get(baseUploadDir, storagePath)
        val fileExtension = file.filename().substringAfterLast('.', "")
        val filename = "${UUID.randomUUID()}.${fileExtension}"
        val filePath = uploadDir.resolve(filename)

        return Mono.fromCallable {
            Files.createDirectories(uploadDir)
        }.subscribeOn(Schedulers.boundedElastic()).then(file.transferTo(filePath)).thenReturn(filename)
            .doOnSuccess { savedFilename ->
                logger.info("File stored successfully: $savedFilename at $filePath")
            }.doOnError { error ->
                logger.error("Failed to store file: ${file.filename()} in path: $storagePath", error)
            }
    }

    override fun delete(filename: String, storagePath: String): Mono<Boolean> {
        val uploadDir = Paths.get(baseUploadDir, storagePath)
        val filePath = uploadDir.resolve(filename)

        return Mono.fromCallable {
            Files.deleteIfExists(filePath)
        }.subscribeOn(Schedulers.boundedElastic()).doOnSuccess { deleted ->
                if (deleted) {
                    logger.info("File deleted successfully: $filename from path: $storagePath")
                } else {
                    logger.warn("File not found for deletion: $filename in path: $storagePath")
                }
            }.doOnError { error ->
                logger.error("Failed to delete file: $filename from path: $storagePath", error)
            }
    }

    override fun getFileUrl(filename: String, urlPath: String): String {
        return "/$urlPath/$filename"
    }
}