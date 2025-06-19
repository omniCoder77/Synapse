package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class ProductPricingResponse(
    val basePrice: Long,
    val salePrice: Long?,
    val costPrice: Long?,
    val currency: String,
    val taxClass: String?,
    val taxIncluded: Boolean,
    val priceValidFrom: Long?,
    val priceValidTo: Long?,
    val bulkPricing: List<BulkPricingResponse>,
)
