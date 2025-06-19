package com.ethyllium.productservice.infrastructure.web.rest.dto.request

import com.ethyllium.productservice.infrastructure.persistence.entity.ProductMediaDocument

data class ProductMediaRequest(
    val images: List<ProductImageRequest> = emptyList(),
    val videos: List<ProductVideoRequest> = emptyList(),
    val documents: List<ProductDocumentRequest> = emptyList(),
    val primaryImageId: String? = null
) {
    fun toDocument() = ProductMediaDocument(
        images = images.map { it.toDocument() },
        videos = videos.map { it.toDocument() },
        documents = documents.map { it.toDocument() },
        primaryImageUrl = primaryImageId
    )
}