package com.ethyllium.searchservice.domain.model

import java.time.LocalDateTime

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String? = null,
    val sku: String,
    val barcode: String? = null,
    val brand: SearchBrand,
    val category: SearchCategory,
    val sellerId: String,
    val pricing: SearchPricing,
    val inventory: SearchInventory,
    val specifications: SearchSpecifications,
    val media: SearchMedia,
    val seo: SearchSEO,
    val tags: Set<String> = emptySet(),
    val status: ProductStatus,
    val visibility: ProductVisibility,
    val variantCode: String,
    val metadata: SearchMetadata,
    val analytics: SearchAnalytics,
    val averageRating: Double = 0.0
) {
    data class SearchBrand(
        val name: String,
        val logoUrl: String
    )

    data class SearchCategory(
        val id: String,
        val name: String,
        val slug: String,
        val parentId: String? = null,
        val path: String,
        val level: Int = 0
    )

    data class SearchPricing(
        val basePrice: Long,
        val salePrice: Long? = null,
        val currency: String = "USD",
        val priceValidFrom: LocalDateTime? = null,
        val priceValidTo: LocalDateTime? = null
    )

    data class SearchInventory(
        val stockQuantity: Int = 0,
        val availableQuantity: Int = 0,
        val stockStatus: StockStatus = StockStatus.IN_STOCK,
        val lowStockThreshold: Int = 0
    )

    data class SearchSpecifications(
        val weight: SearchWeight? = null,
        val dimensions: SearchDimensions? = null,
        val color: String? = null,
        val material: String? = null,
        val customAttributes: Map<String, Any> = emptyMap(),
        val technicalSpecs: Map<String, String> = emptyMap()
    ) {
        data class SearchWeight(
            val value: Long,
            val unit: WeightUnit = WeightUnit.KG
        )

        data class SearchDimensions(
            val length: Long,
            val width: Long,
            val height: Long,
            val unit: DimensionUnit = DimensionUnit.CM
        )
    }

    data class SearchMedia(
        val images: List<SearchImage> = emptyList(),
        val primaryImageUrl: String? = null
    ) {
        data class SearchImage(
            val url: String,
            val alt: String? = null
        )
    }

    data class SearchSEO(
        val metaTitle: String? = null,
        val metaDescription: String? = null,
        val metaKeywords: Set<String> = emptySet(),
        val slug: String
    )

    data class SearchMetadata(
        val externalIds: Map<String, String> = emptyMap(),
        val flags: Set<ProductFlag> = emptySet()
    )

    data class SearchAnalytics(
        val views: Long = 0,
        val clicks: Long = 0,
        val conversions: Long = 0,
        val wishlistAdds: Long = 0,
        val cartAdds: Long = 0
    )
}

enum class ProductStatus {
    DRAFT, ACTIVE, INACTIVE, ARCHIVED, OUT_OF_STOCK, DISCONTINUED
}

enum class ProductVisibility {
    PUBLIC, PRIVATE, HIDDEN, PASSWORD_PROTECTED
}

enum class StockStatus {
    IN_STOCK, LOW_STOCK, OUT_OF_STOCK, BACKORDER, PREORDER
}

enum class WeightUnit {
    G, KG, LB, OZ
}

enum class DimensionUnit {
    MM, CM, M, IN, FT
}

enum class ProductFlag {
    FEATURED, BESTSELLER, NEW_ARRIVAL, ON_SALE, LIMITED_EDITION, EXCLUSIVE
}