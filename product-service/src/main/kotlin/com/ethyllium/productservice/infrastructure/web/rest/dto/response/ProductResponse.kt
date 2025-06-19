package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String? = null,
    val sku: String,
    val barcode: String? = null,
    var brandName: String,
    var categoryName: String,
    var categoryPath: String,
    val sellerId: String,
    val pricing: ProductPricingResponse,
    var inventory: ProductInventoryResponse,
    val specifications: ProductSpecificationsResponse,
    val media: ProductMediaResponse,
    val seo: ProductSEOResponse,
    val shipping: ProductShippingResponse,
    val averageRating: Long = 0,
    val totalReviews: Int = 0,
    val variantCode: String,
    val tags: Set<String> = emptySet(),
    val productStatus: String,
    val visibility: String,
    val facets: Map<String, Any> = emptyMap()
)