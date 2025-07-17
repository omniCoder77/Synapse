package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class OpenGraphDataResponse(
    val title: String,
    val description: String,
    val image: String?,
    val type: String
)
