package com.ethyllium.searchservice.infrastructure.elasticsearch.entity

import com.ethyllium.searchservice.application.dto.BrandDTO
import com.ethyllium.searchservice.application.dto.CategoryDTO
import com.ethyllium.searchservice.application.dto.ProductPriceDTO
import com.ethyllium.searchservice.application.dto.ProductSummaryDTO
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "products")
data class SearchProduct(
    @Id @Field(type = FieldType.Keyword) val id: String,
    @Field(type = FieldType.Text, analyzer = "english") val name: String,
    @Field(type = FieldType.Text, analyzer = "english") val description: String,
    @Field(type = FieldType.Text, analyzer = "english") val shortDescription: String? = null,
    @Field(type = FieldType.Keyword) val sku: String,
    @Field(type = FieldType.Keyword) val barcode: String? = null,
    @Field(type = FieldType.Object) val brand: SearchBrand,
    @Field(type = FieldType.Object) val category: SearchCategory,
    @Field(type = FieldType.Keyword) val sellerId: String,
    @Field(type = FieldType.Object) val pricing: SearchPricing,
    @Field(type = FieldType.Object) val inventory: SearchInventory,
    @Field(type = FieldType.Object) val specifications: SearchSpecifications,
    @Field(type = FieldType.Object) val media: SearchMedia,
    @Field(type = FieldType.Object) val seo: SearchSEO,
    @Field(type = FieldType.Keyword) val tags: Set<String> = emptySet(),
    @Field(type = FieldType.Keyword) val status: ProductStatus,
    @Field(type = FieldType.Keyword) val visibility: ProductVisibility,
    @Field(type = FieldType.Keyword) val variantCode: String,
    @Field(type = FieldType.Object) val metadata: SearchMetadata,
    @Field(type = FieldType.Object) val analytics: SearchAnalytics,
    @Field(type = FieldType.Double) val rating: Double
) {

    fun toDTO() = ProductSummaryDTO(
        id = id,
        name = name,
        description = description,
        price = pricing.toDTO(),
        brand = brand.toDTO(),
        category = category.toDTO(),
        imageUrl = media.primaryImageUrl,
        stockStatus = status.name,
        rating = rating
    )

    @Document(indexName = "products")
    data class SearchBrand(
        @Field(type = FieldType.Keyword) val id: String,
        @Field(type = FieldType.Text, analyzer = "english") val name: String,
        val logoUrl: String?,
    ) {
        fun toDTO() = BrandDTO(
            id = id,
            name = name,
            logoUrl = logoUrl
        )
    }

    @Document(indexName = "products")
    data class SearchCategory(
        @Field(type = FieldType.Text, analyzer = "english") val name: String,
        @Field(type = FieldType.Keyword) val parentName: String? = null,
        @Field(type = FieldType.Keyword) val path: String,
        @Field(type = FieldType.Integer) val level: Int = 0
    ) {
        fun toDTO() = CategoryDTO(
            name = name,
            parentName = parentName,
            path = path,
            level = level
        )
    }

    @Document(indexName = "products")
    data class SearchPricing(
        @Field(type = FieldType.Long) val basePrice: Long,
        @Field(type = FieldType.Long) val salePrice: Long? = null,
        @Field(type = FieldType.Keyword) val currency: String = "USD",
        @Field(
            type = FieldType.Date, format = [DateFormat.date_hour_minute_second]
        ) val priceValidFrom: LocalDateTime? = null,
        @Field(
            type = FieldType.Date, format = [DateFormat.date_hour_minute_second]
        ) val priceValidTo: LocalDateTime? = null
    ) {
        fun toDTO() = ProductPriceDTO(
            basePrice = basePrice,
            salePrice = salePrice,
            currency = currency,
        )
    }

    @Document(indexName = "products")
    data class SearchInventory(
        @Field(type = FieldType.Integer) val stockQuantity: Int = 0,
        @Field(type = FieldType.Integer) val availableQuantity: Int = 0,
        @Field(type = FieldType.Keyword) val stockStatus: StockStatus = StockStatus.IN_STOCK
    )

    @Document(indexName = "products")
    data class SearchSpecifications(
        @Field(type = FieldType.Object) val weight: SearchWeight? = null,
        @Field(type = FieldType.Object) val dimensions: SearchDimensions? = null,
        @Field(type = FieldType.Keyword) val color: String? = null,
        @Field(type = FieldType.Keyword) val material: String? = null,
        @Field(type = FieldType.Flattened) val customAttributes: Map<String, Any> = emptyMap(),
        @Field(type = FieldType.Flattened) val technicalSpecs: Map<String, String> = emptyMap()
    ) {
        @Document(indexName = "products")
        data class SearchWeight(
            @Field(type = FieldType.Long) val value: Long,
            @Field(type = FieldType.Keyword) val unit: WeightUnit = WeightUnit.KG
        )

        @Document(indexName = "products")
        data class SearchDimensions(
            @Field(type = FieldType.Long) val length: Long,
            @Field(type = FieldType.Long) val width: Long,
            @Field(type = FieldType.Long) val height: Long,
            @Field(type = FieldType.Keyword) val unit: DimensionUnit = DimensionUnit.CM
        )
    }

    @Document(indexName = "products")
    data class SearchMedia(
        @Field(type = FieldType.Nested) val images: List<SearchImage> = emptyList(),
        @Field(type = FieldType.Keyword) val primaryImageUrl: String? = null
    ) {
        @Document(indexName = "products")
        data class SearchImage(
            @Field(type = FieldType.Keyword) val url: String, @Field(type = FieldType.Text) val alt: String? = null
        )
    }

    @Document(indexName = "products")
    data class SearchSEO(
        @Field(type = FieldType.Text) val metaTitle: String? = null,
        @Field(type = FieldType.Text) val metaDescription: String? = null,
        @Field(type = FieldType.Keyword) val metaKeywords: Set<String> = emptySet(),
        @Field(type = FieldType.Keyword) val slug: String
    )

    @Document(indexName = "products")
    data class SearchMetadata(
        @Field(type = FieldType.Flattened) val externalIds: Map<String, String> = emptyMap(),
        @Field(type = FieldType.Keyword) val flags: Set<ProductFlag> = emptySet()
    )

    @Document(indexName = "products")
    data class SearchAnalytics(
        @Field(type = FieldType.Long) val views: Long = 0,
        @Field(type = FieldType.Long) val clicks: Long = 0,
        @Field(type = FieldType.Long) val conversions: Long = 0,
        @Field(type = FieldType.Long) val wishlistAdds: Long = 0,
        @Field(type = FieldType.Long) val cartAdds: Long = 0
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