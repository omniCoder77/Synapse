package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity

import com.ethyllium.productservice.domain.model.Brand
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "brands")
data class BrandDocument(
    @Id val brandId: String = "",
    @Indexed(unique = true) val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val website: String? = null,
    val slug: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Brand = Brand(
        name = name, description = description, logoUrl = logoUrl, website = website, slug = slug, id = brandId
    )
}

fun Brand.toDocument(): BrandDocument = BrandDocument(
    name = name, description = description, logoUrl = logoUrl, website = website, slug = slug
)