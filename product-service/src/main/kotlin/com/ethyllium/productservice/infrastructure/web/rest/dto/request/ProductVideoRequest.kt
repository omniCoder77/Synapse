package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.domain.entity.VideoType
import com.ethyllium.productservice.infrastructure.persistence.entity.ProductVideoDocument
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class ProductVideoRequest(
    @field:NotBlank(message = "Video URL is required") @field:Pattern(
        regexp = "^https?://.*", message = "Video URL must be a valid HTTP/HTTPS URL"
    ) val url: String,

    val title: String? = null, val description: String? = null, val thumbnailUrl: String? = null,

    @field:Min(value = 1, message = "Duration must be at least 1 second") val duration: Int? = null,

    val type: VideoType = VideoType.PRODUCT_DEMO
) {
    fun toDocument() = ProductVideoDocument(
        url = url,
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        duration = duration,
        type = type.name
    )
}