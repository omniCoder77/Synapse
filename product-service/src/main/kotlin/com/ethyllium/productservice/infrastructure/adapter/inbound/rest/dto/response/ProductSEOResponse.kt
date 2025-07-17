package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class ProductSEOResponse(
    val metaTitle: String?,
    val metaDescription: String?,
    val metaKeywords: Set<String>,
    val canonicalUrl: String?,
    val openGraphData: OpenGraphDataResponse?,
    val structuredData: Map<String, Any>
)
