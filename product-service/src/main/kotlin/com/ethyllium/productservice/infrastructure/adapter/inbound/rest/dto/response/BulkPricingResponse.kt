package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class BulkPricingResponse(
    val minQuantity: Int,
    val maxQuantity: Int?,
    val price: Long,
    val discountPercentage: Long?
)
