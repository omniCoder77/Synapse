package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class DynamicPricingResponse(
    val enabled: Boolean,
    val parameters: Map<String, Any>
)
