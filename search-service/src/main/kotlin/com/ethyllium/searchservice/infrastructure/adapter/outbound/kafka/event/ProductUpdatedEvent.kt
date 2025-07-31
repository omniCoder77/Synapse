package com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka.event

import com.ethyllium.searchservice.domain.model.*
import java.time.LocalDateTime

data class ProductUpdatedEvent(
    val id: String,
    val name: String?,
    val description: String?,
    val shortDescription: String? = null,
    val sku: String?,
    val barcode: String? = null,
    val sellerId: String?,
    val variantCode: String?,
    val tags: Set<String> = emptySet(),
    val status: ProductStatus?,
    val visibility: ProductVisibility?,
    val rating: Double?,

    val brandId: String?,
    val categoryId: String?,
    val pricingBasePrice: Long?,
    val pricingSalePrice: Long? = null,
    val pricingCurrency: String = "USD",
    val pricingPriceValidFrom: LocalDateTime? = null,
    val pricingPriceValidTo: LocalDateTime? = null,

    val inventoryStockQuantity: Int?,
    val inventoryAvailableQuantity: Int?,
    val inventoryLowStockThreshold: Int?,
    val inventoryStockStatus: StockStatus?,

    val specificationsWeightValue: Long?,
    val specificationsWeightUnit: WeightUnit?,
    val specificationsDimensionsLength: Long?,
    val specificationsDimensionsWidth: Long?,
    val specificationsDimensionsHeight: Long?,
    val specificationsDimensionsUnit: DimensionUnit?,
    val specificationsColor: String?,
    val specificationsMaterial: String?,
    val specificationsCustomAttributes: Map<String, Any>?,
    val specificationsTechnicalSpecs: Map<String, String>?,

    val mediaPrimaryImageUrl: String? = null,
    val mediaImages: List<ProductImage> = emptyList(),

    val seoMetaTitle: String?,
    val seoMetaDescription: String?,
    val seoMetaKeywords: Set<String>?,
    val seoSlug: String?,

    val metadataExternalIds: Map<String, String>?,
    val metadataFlags: Set<ProductFlag>?,

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