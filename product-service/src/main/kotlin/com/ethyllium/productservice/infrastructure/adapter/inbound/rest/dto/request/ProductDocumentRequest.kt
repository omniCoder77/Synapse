package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.domain.model.DocumentType
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.ProductDocumentFile
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ProductDocumentRequest(
    @field:NotBlank(message = "Document URL is required") @field:Pattern(
        regexp = "^https?://.*",
        message = "Document URL must be a valid HTTP/HTTPS URL"
    ) val url: String,

    @field:NotBlank(message = "Document name is required") val name: String,

    val type: DocumentType,

    @field:Min(value = 1, message = "File size must be at least 1 byte") val size: Long? = null,

    val mimeType: String? = null
) {
    fun toDocument() = ProductDocumentFile(
        url = url,
        name = name,
        size = size,
        type = type.name,
        mimeType = mimeType
    )
}
