package com.ethyllium.searchservice.infrastructure.adapter.outbound.elasticsearch.entity

import com.ethyllium.searchservice.application.dto.BrandDTO
import com.ethyllium.searchservice.application.dto.CategoryDTO
import com.ethyllium.searchservice.application.dto.ProductPriceDTO
import com.ethyllium.searchservice.application.dto.ProductSummaryDTO
import com.ethyllium.searchservice.domain.model.*
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime
import java.util.*

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

    fun toDomain(): Product = Product(
        id = id,
        name = name,
        description = description,
        shortDescription = shortDescription,
        sku = sku,
        barcode = barcode,
        brand = brand.toDomain(),
        category = category.toDomain(),
        sellerId = sellerId,
        pricing = pricing.toDomain(),
        inventory = inventory.toDomain(),
        specifications = specifications.toDomain(),
        media = media.toDomain(),
        seo = seo.toDomain(),
        tags = tags,
        status = ProductStatus.valueOf(status.name),
        visibility = ProductVisibility.valueOf(visibility.name),
        variantCode = variantCode,
        metadata = metadata.toDomain(),
        analytics = analytics.toDomain(),
        averageRating = rating
    )

    @Document(indexName = "brands")
    data class SearchBrand(
        @Field(type = FieldType.Keyword) val id: String = UUID.randomUUID().toString(),
        @Field(type = FieldType.Text, analyzer = "english") val name: String,
        val logoUrl: String?,
    ) {
        fun toDTO() = BrandDTO(
            id = id, name = name, logoUrl = logoUrl
        )

        fun toDomain() = Product.SearchBrand(
            id = id, name = name, logoUrl = logoUrl
        )
    }

    @Document(indexName = "category")
    data class SearchCategory(
        @Id val id: String = UUID.randomUUID().toString(),
        @Field(type = FieldType.Text, analyzer = "english") val name: String,
        @Field(type = FieldType.Keyword) val path: String,
        @Field(type = FieldType.Integer) val level: Int = 0,
    ) {

        fun toDTO() = CategoryDTO(
            name = name, path = path, level = level
        )

        fun toDomain() = Product.SearchCategory(
            id = id, name = name, path = path, level = level
        )
    }

    @Document(indexName = "pricing")
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

    @Document(indexName = "inventory")
    data class SearchInventory(
        @Field(type = FieldType.Integer) val stockQuantity: Int = 0,
        @Field(type = FieldType.Integer) val availableQuantity: Int = 0,
        @Field(type = FieldType.Keyword) val stockStatus: StockStatus = StockStatus.IN_STOCK
    )

    @Document(indexName = "specifications")
    data class SearchSpecifications(
        @Field(type = FieldType.Object) val weight: SearchWeight? = null,
        @Field(type = FieldType.Object) val dimensions: SearchDimensions? = null,
        @Field(type = FieldType.Keyword) val color: String? = null,
        @Field(type = FieldType.Keyword) val material: String? = null,
        @Field(type = FieldType.Flattened) val customAttributes: Map<String, Any> = emptyMap(),
        @Field(type = FieldType.Flattened) val technicalSpecs: Map<String, String> = emptyMap()
    ) {
        @Document(indexName = "weight")
        data class SearchWeight(
            @Field(type = FieldType.Long) val value: Long,
            @Field(type = FieldType.Keyword) val unit: WeightUnit = WeightUnit.KG
        )

        @Document(indexName = "dimensions")
        data class SearchDimensions(
            @Field(type = FieldType.Long) val length: Long,
            @Field(type = FieldType.Long) val width: Long,
            @Field(type = FieldType.Long) val height: Long,
            @Field(type = FieldType.Keyword) val unit: DimensionUnit = DimensionUnit.CM
        )
    }

    @Document(indexName = "media")
    data class SearchMedia(
        @Field(type = FieldType.Nested) val images: List<SearchImage> = emptyList(),
        @Field(type = FieldType.Keyword) val primaryImageUrl: String? = null
    ) {
        @Document(indexName = "image")
        data class SearchImage(
            @Field(type = FieldType.Keyword) val url: String, @Field(type = FieldType.Text) val alt: String? = null
        )
    }

    @Document(indexName = "seo")
    data class SearchSEO(
        @Field(type = FieldType.Text) val metaTitle: String? = null,
        @Field(type = FieldType.Text) val metaDescription: String? = null,
        @Field(type = FieldType.Keyword) val metaKeywords: Set<String> = emptySet(),
        @Field(type = FieldType.Keyword) val slug: String
    )

    @Document(indexName = "metadata")
    data class SearchMetadata(
        @Field(type = FieldType.Flattened) val externalIds: Map<String, String> = emptyMap(),
        @Field(type = FieldType.Keyword) val flags: Set<ProductFlag> = emptySet()
    )

    @Document(indexName = "analytics")
    data class SearchAnalytics(
        @Field(type = FieldType.Long) val views: Long = 0,
        @Field(type = FieldType.Long) val clicks: Long = 0,
        @Field(type = FieldType.Long) val conversions: Long = 0,
        @Field(type = FieldType.Long) val wishlistAdds: Long = 0,
        @Field(type = FieldType.Long) val cartAdds: Long = 0
    )
}

fun Product.SearchBrand.toSearchDocument() = SearchProduct.SearchBrand(
    id = id,
    name = name,
    logoUrl = logoUrl
)

private fun SearchProduct.SearchPricing.toDomain(): Product.SearchPricing = Product.SearchPricing(
    basePrice = basePrice,
    salePrice = salePrice,
    currency = currency,
    priceValidFrom = priceValidFrom,
    priceValidTo = priceValidTo
)

private fun SearchProduct.SearchInventory.toDomain(): Product.SearchInventory = Product.SearchInventory(
    stockQuantity = stockQuantity,
    availableQuantity = availableQuantity,
    stockStatus = StockStatus.valueOf(stockStatus.name),
    lowStockThreshold = 0
)

private fun SearchProduct.SearchSpecifications.toDomain(): Product.SearchSpecifications = Product.SearchSpecifications(
    weight = weight?.let { toDomain(it) },
    dimensions = dimensions?.let { toDomain(it) },
    color = color,
    material = material,
    customAttributes = customAttributes,
    technicalSpecs = technicalSpecs
)

private fun toDomain(weight: SearchProduct.SearchSpecifications.SearchWeight): Product.SearchSpecifications.SearchWeight =
    Product.SearchSpecifications.SearchWeight(value = weight.value, unit = WeightUnit.valueOf(weight.unit.name))

private fun toDomain(dimensions: SearchProduct.SearchSpecifications.SearchDimensions): Product.SearchSpecifications.SearchDimensions =
    Product.SearchSpecifications.SearchDimensions(
        length = dimensions.length,
        width = dimensions.width,
        height = dimensions.height,
        unit = DimensionUnit.valueOf(dimensions.unit.name)
    )

private fun SearchProduct.SearchMedia.toDomain(): Product.SearchMedia =
    Product.SearchMedia(images = images.map { toDomain(it) }, primaryImageUrl = primaryImageUrl)

private fun toDomain(image: SearchProduct.SearchMedia.SearchImage): Product.SearchMedia.SearchImage =
    Product.SearchMedia.SearchImage(url = image.url, alt = image.alt)

private fun SearchProduct.SearchSEO.toDomain(): Product.SearchSEO = Product.SearchSEO(
    metaTitle = metaTitle, metaDescription = metaDescription, metaKeywords = metaKeywords, slug = slug
)

private fun SearchProduct.SearchMetadata.toDomain(): Product.SearchMetadata = Product.SearchMetadata(
    externalIds = externalIds, flags = flags.map { ProductFlag.valueOf(it.name) }.toSet()
)

private fun SearchProduct.SearchAnalytics.toDomain(): Product.SearchAnalytics = Product.SearchAnalytics(
    views = views, clicks = clicks, conversions = conversions, wishlistAdds = wishlistAdds, cartAdds = cartAdds
)