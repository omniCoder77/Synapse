package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response

import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.ProductMediaResponse
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.ProductSEOResponse
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response.ProductSpecificationsResponse

data class ProductResponse(
    val id: String,
    val name: String,
    val categoryId: String?,
    val description: String,
    val shortDescription: String? = null,
    val sku: String,
    val barcode: String? = null,
    var categoryPath: String,
    val sellerId: String,
    val pricing: ProductPricingResponse,
    var inventory: ProductInventoryResponse,
    val specifications: ProductSpecificationsResponse,
    val media: ProductMediaResponse,
    val seo: ProductSEOResponse,
    val shipping: ProductShippingResponse,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val variantCode: String?,
    val tags: Set<String> = emptySet(),
    val productStatus: String,
    val visibility: String,
    val facets: Map<String, Any> = emptyMap()
)