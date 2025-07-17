package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.WeightUnit

data class WeightResponse(
    val value: Long,
    val unit: WeightUnit
)
