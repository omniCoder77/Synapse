package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

import com.ethyllium.productservice.domain.model.DimensionUnit
import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.ProductFlag
import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.domain.model.StockStatus
import com.ethyllium.productservice.domain.model.WeightUnit
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toKafkaProductImage
import java.time.LocalDateTime

data class ProductEvent(
    val id: String?,
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

    val specificationsWeightValue: Long? = null,
    val specificationsWeightUnit: WeightUnit? = null,
    val specificationsDimensionsLength: Long? = null,
    val specificationsDimensionsWidth: Long? = null,
    val specificationsDimensionsHeight: Long? = null,
    val specificationsDimensionsUnit: DimensionUnit? = null,
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
): Event {

    companion object {
        const val KAFKA_PRODUCER = "product.created"
    }

    data class ProductImage(
        val url: String,
        val alt: String? = null
    )
}
fun Product.toCreatedKafkaEvent(): ProductEvent {
    return ProductEvent(
        id = this.id,
        name = this.name,
        description = this.description,
        shortDescription = this.shortDescription,
        sku = this.sku,
        barcode = this.barcode,
        sellerId = this.seller.id,
        variantCode = this.variantCode,
        tags = this.tags,
        status = this.status,
        visibility = this.visibility,
        rating = this.reviews.averageRating.toDouble(),
        brandId = this.brand.id ?: "",
        categoryId = this.category.id!!,
        pricingBasePrice = this.pricing.basePrice,
        pricingSalePrice = this.pricing.salePrice,
        pricingCurrency = this.pricing.currency,
        pricingPriceValidFrom = this.pricing.priceValidFrom,
        pricingPriceValidTo = this.pricing.priceValidTo,
        inventoryStockQuantity = this.inventory.stockQuantity,
        inventoryAvailableQuantity = this.inventory.availableQuantity,
        inventoryStockStatus = this.inventory.stockStatus,
        specificationsWeightValue = this.specifications.weight?.value,
        specificationsWeightUnit = this.specifications.weight?.unit,
        specificationsDimensionsLength = this.specifications.dimensions?.length,
        specificationsDimensionsWidth = this.specifications.dimensions?.width,
        specificationsDimensionsHeight = this.specifications.dimensions?.height,
        specificationsDimensionsUnit = this.specifications.dimensions?.unit,
        specificationsColor = this.specifications.color,
        specificationsMaterial = this.specifications.material,
        specificationsCustomAttributes = this.specifications.customAttributes,
        specificationsTechnicalSpecs = this.specifications.technicalSpecs,
        mediaPrimaryImageUrl = this.media.primaryImageId,
        mediaImages = this.media.images.map { it.toKafkaProductImage() },
        seoMetaTitle = this.seo.metaTitle,
        seoMetaDescription = this.seo.metaDescription,
        seoMetaKeywords = this.seo.metaKeywords,
        seoSlug = this.seo.slug,
        metadataExternalIds = this.metadata.externalIds,
        metadataFlags = this.metadata.flags,
        analyticsViews = this.metadata.analytics.views,
        analyticsClicks = this.metadata.analytics.clicks,
        analyticsConversions = this.metadata.analytics.conversions,
        analyticsWishlistAdds = this.metadata.analytics.wishlistAdds,
        analyticsCartAdds = this.metadata.analytics.cartAdds
    )
}