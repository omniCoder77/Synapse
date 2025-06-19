package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.VideoType

data class ProductVideoResponse(
    val url: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val duration: Int?,
    val type: VideoType
)
