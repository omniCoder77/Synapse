package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.WeightUnit

data class WeightResponse(
    val value: Long,
    val unit: WeightUnit
)
