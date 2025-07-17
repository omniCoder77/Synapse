package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.OpenGraphDataDocument
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OpenGraphDataRequest(
    @field:NotBlank(message = "Open Graph title is required") val title: String,

    @field:NotBlank(message = "Open Graph description is required") val description: String,

    @field:Pattern(
        regexp = "^https?://.*",
        message = "Open Graph image must be a valid HTTP/HTTPS URL"
    ) val image: String? = null,

    val type: String = "product"
) {
    fun toDocument() = OpenGraphDataDocument(
        title = title,
        description = description,
        image = image,
        type = type
    )
}