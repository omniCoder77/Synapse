package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.ProductFlag

data class ProductMetadataResponse(
    val source: String?,
    val importId: String?,
    val externalIds: Map<String, String>,
    val customFields: Map<String, Any>,
    val flags: Set<ProductFlag>,
    val analytics: ProductAnalyticsResponse
)
