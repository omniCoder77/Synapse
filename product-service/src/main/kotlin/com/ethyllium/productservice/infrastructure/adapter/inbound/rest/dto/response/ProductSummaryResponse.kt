package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.response

import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.domain.model.StockStatus

data class ProductSummaryResponse(
    val id: String,
    val name: String,
    val shortDescription: String?,
    val sku: String,
    val brandName: String,
    val categoryName: String,
    val sellerId: String,
    val basePrice: Double,
    val salePrice: Double?,
    val currency: String,
    val averageRating: Double,
    val totalReviews: Int,
    val primaryImageUrl: String?,
    val stockStatus: StockStatus,
    val status: ProductStatus,
    val visibility: ProductVisibility,
    val tags: Set<String>,
)
