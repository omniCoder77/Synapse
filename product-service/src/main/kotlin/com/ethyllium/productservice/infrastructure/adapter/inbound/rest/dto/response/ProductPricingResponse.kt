package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.BulkPricingResponse

data class ProductPricingResponse(
    val basePrice: Double,
    val salePrice: Double?,
    val costPrice: Double?,
    val currency: String,
    val taxClass: String,
    val taxIncluded: Boolean,
    val priceValidFrom: Long?,
    val priceValidTo: Long?,
    val bulkPricing: List<BulkPricingResponse>,
)
