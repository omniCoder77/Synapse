package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.DocumentType

data class ProductDocumentResponse(
    val url: String,
    val name: String,
    val type: DocumentType,
    val size: Long?,
    val mimeType: String?
)
