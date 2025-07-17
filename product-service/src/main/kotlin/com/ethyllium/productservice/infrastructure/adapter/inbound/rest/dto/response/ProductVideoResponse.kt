package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.VideoType

data class ProductVideoResponse(
    val url: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val duration: Int?,
    val type: VideoType
)
