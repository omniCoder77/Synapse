package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request

import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.ProductMediaDocument

data class ProductMediaRequest(
    val images: List<ProductImageRequest> = emptyList(),
    val videos: List<ProductVideoRequest> = emptyList(),
    val documents: List<ProductDocumentRequest> = emptyList()
) {
    fun toDocument() = ProductMediaDocument(
        images = images.map { it.toDocument() },
        videos = videos.map { it.toDocument() },
        documents = documents.map { it.toDocument() },
    )
}