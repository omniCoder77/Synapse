package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ProductDocumentResponse
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ProductImageResponse
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ProductVideoResponse

data class ProductMediaResponse(
    val images: List<ProductImageResponse>,
    val videos: List<ProductVideoResponse>,
    val documents: List<ProductDocumentResponse>,
    val primaryImageUrl: String?
)
