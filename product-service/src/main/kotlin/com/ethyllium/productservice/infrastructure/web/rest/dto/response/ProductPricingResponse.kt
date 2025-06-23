package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.TaxClass

data class ProductPricingResponse(
    val basePrice: Double,
    val salePrice: Double?,
    val costPrice: Double?,
    val currency: String,
    val taxClass: TaxClass,
    val taxIncluded: Boolean,
    val priceValidFrom: Long?,
    val priceValidTo: Long?,
    val bulkPricing: List<BulkPricingResponse>,
)
