package com.ethyllium.productservice.infrastructure.web.rest.dto.response

import com.ethyllium.productservice.domain.entity.ProductStatus
import com.ethyllium.productservice.domain.entity.ProductVisibility
import com.ethyllium.productservice.domain.entity.StockStatus

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
    val averageRating: Long,
    val totalReviews: Int,
    val primaryImageUrl: String?,
    val stockStatus: StockStatus,
    val status: ProductStatus,
    val visibility: ProductVisibility,
    val tags: Set<String>,
)
