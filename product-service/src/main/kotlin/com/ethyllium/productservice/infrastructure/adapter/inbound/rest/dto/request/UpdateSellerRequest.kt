package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

data class UpdateSellerRequest(
    val businessName: String? = null,
    val displayName: String? = null,
    val phone: String? = null,
)