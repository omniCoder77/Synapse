package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.domain.model.ImageType
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.ProductImageDocument
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ProductImageRequest(
    @field:NotBlank(message = "Image URL is required") @field:Pattern(
        regexp = "^https?://.*",
        message = "Image URL must be a valid HTTP/HTTPS URL"
    ) val url: String,

    val alt: String? = null,
    val title: String? = null,

    @field:Min(value = 0, message = "Sort order cannot be negative") val sortOrder: Int = 0,

    val type: ImageType = ImageType.PRODUCT,
    val thumbnailUrl: String? = null,
    val mediumUrl: String? = null,
    val largeUrl: String? = null
) {
    fun toDocument() = ProductImageDocument(
        url = url,
        alt = alt,
        title = title,
        sortOrder = sortOrder,
        type = type.name,
        thumbnailUrl = thumbnailUrl,
        mediumUrl = mediumUrl,
        largeUrl = largeUrl
    )
}