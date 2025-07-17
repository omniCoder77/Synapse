package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class BrandResponse(
    val id: String,
    val name: String,
    val description: String?,
    val logoUrl: String?,
    val website: String?,
    val slug: String?
)