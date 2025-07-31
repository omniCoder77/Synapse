package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

data class ProductDocumentResponse(
    val url: String,
    val name: String,
    val type: String,
    val size: Long?,
    val mimeType: String?
)
