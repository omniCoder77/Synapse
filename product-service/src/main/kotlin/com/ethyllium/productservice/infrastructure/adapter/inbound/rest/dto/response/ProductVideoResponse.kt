package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

data class ProductVideoResponse(
    val url: String,
    val title: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val duration: Int?,
    val type: String
)
