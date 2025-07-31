package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.ProductSEODocument

data class ProductSEORequest(
    val metaTitle: String? = null,
    val metaDescription: String? = null,
    val metaKeywords: Set<String> = emptySet(),
    val canonicalUrl: String? = null,
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