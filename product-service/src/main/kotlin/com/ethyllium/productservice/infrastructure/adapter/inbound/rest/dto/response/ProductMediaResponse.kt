package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

data class ProductMediaResponse(
    val images: List<ProductImageResponse>,
    val videos: List<ProductVideoResponse>,
    val documents: List<ProductDocumentResponse>,
    val primaryImageUrl: String?
)
