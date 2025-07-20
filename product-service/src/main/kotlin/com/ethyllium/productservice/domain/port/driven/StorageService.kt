package com.ethyllium.productservice.domain.port.driven

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface StorageService {
    fun store(file: FilePart, storagePath: String): Mono<String>
    fun delete(filename: String, storagePath: String): Mono<Boolean>
    fun getFileUrl(filename: String, urlPath: String): String
}