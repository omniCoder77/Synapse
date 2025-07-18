package com.ethyllium.searchservice.infrastructure.kafka.util

import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.infrastructure.kafka.event.ProductCreatedEvent
import com.ethyllium.searchservice.infrastructure.kafka.repository.BrandRepository
import com.ethyllium.searchservice.infrastructure.kafka.repository.CategoryRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ProductMapper(
    private val categoryRepository: CategoryRepository, private val brandRepository: BrandRepository
) {
    fun toProduct(createdEvent: ProductCreatedEvent): Product {
        val categoryMono = categoryRepository.findSearchCategoryById(createdEvent.categoryId)
        val brandMono = brandRepository.findSearchBrandById(createdEvent.brandId)
        return Mono.zip(categoryMono, brandMono).map {
            Product(
                id = createdEvent.id,
                name = createdEvent.name,
                description = createdEvent.description,
                shortDescription = createdEvent.shortDescription,
                sku = createdEvent.sku,
                barcode = createdEvent.barcode,
                sellerId = createdEvent.sellerId,
                category = it.t1.toDomain(),
                brand = it.t2.toDomain(),
                pricing = Product.SearchPricing(
                    createdEvent.pricingBasePrice,
                    createdEvent.pricingSalePrice,
                    createdEvent.pricingCurrency,
                    createdEvent.pricingPriceValidFrom,
                    createdEvent.pricingPriceValidTo
                ),
                inventory = Product.SearchInventory(
                    stockQuantity = createdEvent.inventoryStockQuantity,
                    availableQuantity = createdEvent.inventoryAvailableQuantity,
                    stockStatus = createdEvent.inventoryStockStatus,
                    lowStockThreshold = createdEvent.inventoryLowStockThreshold
                ),
                specifications = Product.SearchSpecifications(
                    weight = Product.SearchSpecifications.SearchWeight(
                        createdEvent.specificationsWeightValue, createdEvent.specificationsWeightUnit
                    ), dimensions = Product.SearchSpecifications.SearchDimensions(
                        length = createdEvent.specificationsDimensionsLength,
                        width = createdEvent.specificationsDimensionsWidth,
                        height = createdEvent.specificationsDimensionsHeight,
                        unit = createdEvent.specificationsDimensionsUnit
                    ), color = createdEvent.specificationsColor, material = createdEvent.specificationsMaterial
                ),
                variantCode = createdEvent.variantCode,
                visibility = createdEvent.visibility,
                media = Product.SearchMedia(createdEvent.mediaImages.map {
                    Product.SearchMedia.SearchImage(
                        it.url, it.alt
                    )
                }, createdEvent.mediaPrimaryImageUrl),
                seo = Product.SearchSEO(
                    metaTitle = createdEvent.seoMetaTitle,
                    metaDescription = createdEvent.seoMetaDescription,
                    metaKeywords = createdEvent.seoMetaKeywords,
                    slug = createdEvent.seoSlug
                ),
                averageRating = createdEvent.rating,
                status = createdEvent.status,
                metadata = Product.SearchMetadata(createdEvent.metadataExternalIds, createdEvent.metadataFlags),
                analytics = Product.SearchAnalytics(
                    createdEvent.analyticsViews,
                    createdEvent.analyticsClicks,
                    createdEvent.analyticsConversions,
                    createdEvent.analyticsWishlistAdds,
                    createdEvent.analyticsCartAdds
                ),
            )
        }.block()!!
    }
}