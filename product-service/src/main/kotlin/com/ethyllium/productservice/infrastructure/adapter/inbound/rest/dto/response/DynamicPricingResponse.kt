package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class DynamicPricingResponse(
    val enabled: Boolean,
    val parameters: Map<String, Any>
)
