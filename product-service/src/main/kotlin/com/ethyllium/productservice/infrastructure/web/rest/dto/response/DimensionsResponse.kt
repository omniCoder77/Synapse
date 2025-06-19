package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.DimensionUnit

data class DimensionsResponse(
    val length: Long,
    val width: Long,
    val height: Long,
    val unit: DimensionUnit
)
