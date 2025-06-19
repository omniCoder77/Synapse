package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.ImageType

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
