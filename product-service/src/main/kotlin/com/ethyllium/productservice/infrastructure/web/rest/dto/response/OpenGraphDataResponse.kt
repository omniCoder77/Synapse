package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class OpenGraphDataResponse(
    val title: String,
    val description: String,
    val image: String?,
    val type: String
)
