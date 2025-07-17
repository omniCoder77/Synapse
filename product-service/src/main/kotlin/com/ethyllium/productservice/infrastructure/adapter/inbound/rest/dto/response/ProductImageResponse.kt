package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.ImageType

data class ProductImageResponse(
    val url: String,
    val alt: String?,
    val title: String?,
    val sortOrder: Int,
    val type: ImageType,
    val thumbnailUrl: String?,
    val mediumUrl: String?,
    val largeUrl: String?
)
