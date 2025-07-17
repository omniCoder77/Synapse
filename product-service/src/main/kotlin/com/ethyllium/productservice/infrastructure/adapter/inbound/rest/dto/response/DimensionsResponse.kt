package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.DimensionUnit

data class DimensionsResponse(
    val length: Long,
    val width: Long,
    val height: Long,
    val unit: DimensionUnit
)
