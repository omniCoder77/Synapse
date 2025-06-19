package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.DocumentType

data class ProductDocumentResponse(
    val url: String,
    val name: String,
    val type: DocumentType,
    val size: Long?,
    val mimeType: String?
)
