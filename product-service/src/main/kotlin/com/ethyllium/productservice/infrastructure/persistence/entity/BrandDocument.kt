package com.ethyllium.productservice.infrastructure.persistence.entity

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "brands")
data class BrandDocument(
    @Indexed(unique = true) val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val website: String? = null,
    @Indexed(unique = true) val slug: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)