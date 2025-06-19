package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class ProductMediaResponse(
    val images: List<ProductImageResponse>,
    val videos: List<ProductVideoResponse>,
    val documents: List<ProductDocumentResponse>,
    val primaryImageUrl: String?
)
