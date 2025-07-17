package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class BrandSummaryResponse(
    val id: String,
    val name: String,
    val slug: String,
    val logoUrl: String?
)
