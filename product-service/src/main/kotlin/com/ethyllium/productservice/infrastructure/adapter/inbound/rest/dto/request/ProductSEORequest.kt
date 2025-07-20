package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.ProductSEODocument
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ProductSEORequest(
    @field:Size(max = 60, message = "Meta title must not exceed 60 characters") val metaTitle: String? = null,

    @field:Size(
        max = 160,
        message = "Meta description must not exceed 160 characters"
    ) val metaDescription: String? = null,

    val metaKeywords: Set<String> = emptySet(),

    @field:Pattern(
        regexp = "^https?://.*",
        message = "Canonical URL must be a valid HTTP/HTTPS URL"
    ) val canonicalUrl: String? = null,

    val openGraphData: OpenGraphDataRequest? = null, val structuredData: Map<String, Any> = emptyMap()
) {
    fun toDocument() = ProductSEODocument(
        metaTitle = metaTitle,
        metaDescription = metaDescription,
        metaKeywords = metaKeywords,
        canonicalUrl = canonicalUrl,
        openGraphData = openGraphData?.toDocument(),
        structuredData = structuredData
    )
}