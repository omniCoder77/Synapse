package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.ProductFlag

data class ProductMetadataResponse(
    val source: String?,
    val importId: String?,
    val externalIds: Map<String, String>,
    val customFields: Map<String, Any>,
    val flags: Set<ProductFlag>,
    val analytics: ProductAnalyticsResponse
)
