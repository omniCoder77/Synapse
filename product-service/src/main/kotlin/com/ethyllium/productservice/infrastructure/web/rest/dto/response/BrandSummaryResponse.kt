package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class BrandSummaryResponse(
    val id: String,
    val name: String,
    val slug: String,
    val logoUrl: String?
)
