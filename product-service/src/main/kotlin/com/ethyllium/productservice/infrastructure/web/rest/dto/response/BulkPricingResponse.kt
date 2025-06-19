package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class BulkPricingResponse(
    val minQuantity: Int,
    val maxQuantity: Int?,
    val price: Long,
    val discountPercentage: Long?
)
