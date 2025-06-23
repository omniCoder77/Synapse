package com.ethyllium.productservice.application.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Path
import java.nio.file.Paths


@Configuration
class FileStorageConfig(
    @Value("\${file.upload-dir}") private val uploadDir: String
) {
    @Bean
    fun uploadPath(): Path {
        return Paths.get(uploadDir).toAbsolutePath().normalize()
    }
}