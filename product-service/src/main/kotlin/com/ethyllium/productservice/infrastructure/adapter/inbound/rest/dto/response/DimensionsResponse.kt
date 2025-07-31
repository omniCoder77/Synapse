package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

data class DimensionsResponse(
    val length: Long,
    val width: Long,
    val height: Long,
    val unit: String
)
