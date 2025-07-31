package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

data class ProductImageResponse(
    val url: String,
    val alt: String?,
    val title: String?,
    val sortOrder: Int,
    val type: String,
    val thumbnailUrl: String?,
    val mediumUrl: String?,
    val largeUrl: String?
)
