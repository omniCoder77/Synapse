package com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka.event

import com.ethyllium.searchservice.domain.model.DimensionUnit
import com.ethyllium.searchservice.domain.model.ProductFlag
import com.ethyllium.searchservice.domain.model.ProductStatus
import com.ethyllium.searchservice.domain.model.ProductVisibility
import com.ethyllium.searchservice.domain.model.StockStatus
import com.ethyllium.searchservice.domain.model.WeightUnit
import java.time.LocalDateTime

data class ProductCreatedEvent(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String? = null,
    val sku: String,
    val barcode: String? = null,
    val sellerId: String,
    val variantCode: String,
    val tags: Set<String> = emptySet(),
    val status: ProductStatus,
    val visibility: ProductVisibility,
    val rating: Double,

    val brandId: String,
    val categoryId: String,
    val pricingBasePrice: Long,
    val pricingSalePrice: Long? = null,
    val pricingCurrency: String = "USD",
    val pricingPriceValidFrom: LocalDateTime? = null,
    val pricingPriceValidTo: LocalDateTime? = null,

    val inventoryStockQuantity: Int = 0,
    val inventoryAvailableQuantity: Int = 0,
    val inventoryLowStockThreshold: Int = 0,
    val inventoryStockStatus: StockStatus = StockStatus.IN_STOCK,

    val specificationsWeightValue: Long,
    val specificationsWeightUnit: WeightUnit,
    val specificationsDimensionsLength: Long,
    val specificationsDimensionsWidth: Long,
    val specificationsDimensionsHeight: Long,
    val specificationsDimensionsUnit: DimensionUnit,
    val specificationsColor: String? = null,
    val specificationsMaterial: String? = null,
    val specificationsCustomAttributes: Map<String, Any> = emptyMap(),
    val specificationsTechnicalSpecs: Map<String, String> = emptyMap(),

    val mediaPrimaryImageUrl: String? = null,
    val mediaImages: List<ProductImage> = emptyList(),

    val seoMetaTitle: String? = null,
    val seoMetaDescription: String? = null,
    val seoMetaKeywords: Set<String> = emptySet(),
    val seoSlug: String,

    val metadataExternalIds: Map<String, String> = emptyMap(),
    val metadataFlags: Set<ProductFlag> = emptySet(),

    val analyticsViews: Long = 0,
    val analyticsClicks: Long = 0,
    val analyticsConversions: Long = 0,
    val analyticsWishlistAdds: Long = 0,
    val analyticsCartAdds: Long = 0
) {
    data class ProductImage(
        val url: String, val alt: String? = null
    )
}