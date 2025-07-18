package com.ethyllium.searchservice.infrastructure.kafka

import com.ethyllium.searchservice.domain.model.Product
import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.SearchProduct
import com.ethyllium.searchservice.infrastructure.elasticsearch.entity.toSearchDocument
import com.ethyllium.searchservice.infrastructure.kafka.event.*
import com.ethyllium.searchservice.infrastructure.kafka.util.ProductMapper
import com.ethyllium.searchservice.infrastructure.kafka.util.Topics
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.UpdateQuery
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
class Listener(
    private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate, private val productMapper: ProductMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [Topics.BRAND_CREATED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onBrandCreated(message: String) {
        val brandCreatedEvent = jacksonObjectMapper().readValue(message, BrandCreatedEvent::class.java)
        val brand = Product.SearchBrand(
            id = brandCreatedEvent.id, name = brandCreatedEvent.name, logoUrl = brandCreatedEvent.logoUrl
        )
        reactiveElasticsearchTemplate.save(brand.toSearchDocument()).subscribeOn(Schedulers.boundedElastic())
            .subscribe {
                logger.info("Successfully saved Brand: $brand")
            }
    }

    @KafkaListener(
        topics = [Topics.BRAND_DELETED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onBrandDeleted(message: String) {
        try {
            val brandDeletedEvent = jacksonObjectMapper().readValue(message, BrandDeletedEvent::class.java)

            reactiveElasticsearchTemplate.delete(brandDeletedEvent.brandId, IndexCoordinates.of("brands"))
                .subscribeOn(Schedulers.boundedElastic()).subscribe({
                    logger.info(it)
                }, { error ->
                    logger.error("Failed to delete brand with ID: ${brandDeletedEvent.brandId}", error)
                })
        } catch (e: Exception) {
            logger.error("Error processing brand deletion message: $message", e)
        }
    }

    @KafkaListener(
        topics = [Topics.BRAND_UPDATED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onBrandUpdated(message: String) {
        val brandUpdatedEvent = jacksonObjectMapper().readValue(message, BrandUpdatedEvent::class.java)
        val updateDoc = Document.create().apply {
            brandUpdatedEvent.name?.let { put("name", it) }
            brandUpdatedEvent.logoUrl?.let { put("logoUrl", it) }
            brandUpdatedEvent.description?.let { put("description", it) }
            brandUpdatedEvent.website?.let { put("website", it) }
            brandUpdatedEvent.slug?.let { put("slug", it) }
            brandUpdatedEvent.fileUrl?.let { put("fileUrl", it) }
        }
        if (updateDoc.isNotEmpty()) {
            val updateQuery = UpdateQuery.builder(brandUpdatedEvent.brandId).withDocument(updateDoc).withIndex("brands")
                .withDocAsUpsert(false).build()
            reactiveElasticsearchTemplate.update(updateQuery, IndexCoordinates.of("brands"))
                .subscribeOn(Schedulers.boundedElastic()).subscribe()
        }
    }

    @KafkaListener(
        topics = [Topics.PRODUCT_CREATED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onProductCreated(message: String) {
        val productCreatedEvent = jacksonObjectMapper().readValue(message, ProductCreatedEvent::class.java)
        val product = productMapper.toProduct(productCreatedEvent)
        reactiveElasticsearchTemplate.save(product, IndexCoordinates.of("product"))
            .subscribeOn(Schedulers.boundedElastic()).subscribe()
    }

    @KafkaListener(
        topics = [Topics.PRODUCT_UPDATED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onProductUpdated(message: String) {
        val productUpdatedEvent = jacksonObjectMapper().readValue(message, ProductUpdatedEvent::class.java)
        val updateDoc = Document.create().apply {
            productUpdatedEvent.name?.let { put("name", it) }
            productUpdatedEvent.description?.let { put("description", it) }
            productUpdatedEvent.shortDescription?.let { put("shortDescription", it) }
            productUpdatedEvent.sku?.let { put("sku", it) }
            productUpdatedEvent.barcode?.let { put("barcode", it) }
            productUpdatedEvent.sellerId?.let { put("sellerId", it) }
            productUpdatedEvent.variantCode?.let { put("variantCode", it) }
            productUpdatedEvent.tags.let {
                put(
                    "tags", it
                )
            } // Tags is non-nullable, but let's keep the pattern for consistency
            productUpdatedEvent.status?.let { put("status", it.name) } // Enums often stored as strings
            productUpdatedEvent.visibility?.let { put("visibility", it.name) } // Enums often stored as strings
            productUpdatedEvent.rating?.let { put("rating", it) }

            productUpdatedEvent.brandId?.let { put("brand.id", it) }
            productUpdatedEvent.categoryId?.let { put("category.id", it) }
            productUpdatedEvent.pricingBasePrice?.let { put("pricing.basePrice", it) }
            productUpdatedEvent.pricingSalePrice?.let { put("pricing.salePrice", it) }
            put(
                "pricing.currency", productUpdatedEvent.pricingCurrency
            ) // Currency has a default, so it's always there
            productUpdatedEvent.pricingPriceValidFrom?.let { put("pricing.priceValidFrom", it) }
            productUpdatedEvent.pricingPriceValidTo?.let { put("pricing.priceValidTo", it) }

            productUpdatedEvent.inventoryStockQuantity?.let { put("inventory.stockQuantity", it) }
            productUpdatedEvent.inventoryAvailableQuantity?.let { put("inventory.availableQuantity", it) }
            productUpdatedEvent.inventoryLowStockThreshold?.let { put("inventory.lowStockThreshold", it) }
            productUpdatedEvent.inventoryStockStatus?.let { put("inventory.stockStatus", it.name) }

            productUpdatedEvent.specificationsWeightValue?.let { put("specifications.weight.value", it) }
            productUpdatedEvent.specificationsWeightUnit?.let { put("specifications.weight.unit", it.name) }
            productUpdatedEvent.specificationsDimensionsLength?.let {
                put(
                    "specifications.dimensions.length", it
                )
            }
            productUpdatedEvent.specificationsDimensionsWidth?.let {
                put(
                    "specifications.dimensions.width", it
                )
            }
            productUpdatedEvent.specificationsDimensionsHeight?.let {
                put(
                    "specifications.dimensions.height", it
                )
            }
            productUpdatedEvent.specificationsDimensionsUnit?.let {
                put(
                    "specifications.dimensions.unit", it.name
                )
            }
            productUpdatedEvent.specificationsColor?.let { put("specifications.color", it) }
            productUpdatedEvent.specificationsMaterial?.let { put("specifications.material", it) }
            productUpdatedEvent.specificationsCustomAttributes?.let {
                put(
                    "specifications.customAttributes", it
                )
            }
            productUpdatedEvent.specificationsTechnicalSpecs?.let { put("specifications.technicalSpecs", it) }

            productUpdatedEvent.mediaPrimaryImageUrl?.let { put("media.primaryImageUrl", it) }
            productUpdatedEvent.mediaImages.let { images ->
                if (images.isNotEmpty()) {
                    put("media.images", images.map { mapOf("url" to it.url, "alt" to it.alt) })
                }
            }

            productUpdatedEvent.seoMetaTitle?.let { put("seo.metaTitle", it) }
            productUpdatedEvent.seoMetaDescription?.let { put("seo.metaDescription", it) }
            productUpdatedEvent.seoMetaKeywords?.let { put("seo.metaKeywords", it) }
            productUpdatedEvent.seoSlug?.let { put("seo.slug", it) }

            productUpdatedEvent.metadataExternalIds?.let { put("metadata.externalIds", it) }
            productUpdatedEvent.metadataFlags?.let { flags ->
                if (flags.isNotEmpty()) {
                    put("metadata.flags", flags.map { it.name })
                }
            }

            put("analytics.views", productUpdatedEvent.analyticsViews)
            put("analytics.clicks", productUpdatedEvent.analyticsClicks)
            put("analytics.conversions", productUpdatedEvent.analyticsConversions)
            put("analytics.wishlistAdds", productUpdatedEvent.analyticsWishlistAdds)
            put("analytics.cartAdds", productUpdatedEvent.analyticsCartAdds)
        }
        if (updateDoc.isNotEmpty()) {
            val updateQuery =
                UpdateQuery.builder(productUpdatedEvent.id).withDocument(updateDoc).withDocAsUpsert(false).build()
            reactiveElasticsearchTemplate.update(updateQuery, IndexCoordinates.of("product"))
                .subscribeOn(Schedulers.boundedElastic()).subscribe()
        } else {
            logger.warn("No fields to update for product with ID: ${productUpdatedEvent.id}")
        }
    }

    @KafkaListener(
        topics = [Topics.PRODUCT_DELETED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onProductDeleted(productId: String) {
        reactiveElasticsearchTemplate.delete(productId, IndexCoordinates.of("product"))
            .subscribeOn(Schedulers.boundedElastic()).subscribe()
    }

    @KafkaListener(
        topics = [Topics.CATEGORY_CREATED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onCategoryCreated(message: String) {
        val categoryCreatedEvent = jacksonObjectMapper().readValue(message, CategoryCreatedEvent::class.java)
        val category = SearchProduct.SearchCategory(
            id = categoryCreatedEvent.id,
            name = categoryCreatedEvent.name,
            level = categoryCreatedEvent.level,
            path = categoryCreatedEvent.path,
        )
        reactiveElasticsearchTemplate.save(category, IndexCoordinates.of("category"))
            .subscribeOn(Schedulers.boundedElastic()).subscribe()
    }

    @KafkaListener(
        topics = [Topics.CATEGORY_UPDATED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onCategoryUpdated(message: String) {
        val categoryUpdatedEvent = jacksonObjectMapper().readValue(message, CategoryUpdatedEvent::class.java)
        val updatedDoc = Document.create().apply {
            categoryUpdatedEvent.name?.let { put("name", it) }
            categoryUpdatedEvent.description?.let { put("description", it) }
            categoryUpdatedEvent.slug?.let { put("slug", it) }
            categoryUpdatedEvent.parentId?.let { put("parentId", it) }
        }
        if (updatedDoc.isNotEmpty()) {
            val updateQuery =
                UpdateQuery.builder(categoryUpdatedEvent.categoryId).withDocument(updatedDoc).withDocAsUpsert(false)
                    .build()
            reactiveElasticsearchTemplate.update(updateQuery, IndexCoordinates.of("categories"))
                .subscribeOn(Schedulers.boundedElastic()).subscribe()
        } else {
            logger.warn("No fields to update for category with ID: ${categoryUpdatedEvent.categoryId}")
        }
    }

    @KafkaListener(
        topics = [Topics.CATEGORY_DELETED], groupId = "my-group-id", containerFactory = "kafkaListenerContainerFactory"
    )
    fun onCategoryDeleted(message: String) {
        val categoryDeletedEvent = jacksonObjectMapper().readValue(message, CategoryDeletedEvent::class.java)
        reactiveElasticsearchTemplate.delete(categoryDeletedEvent.categoryId, SearchProduct.SearchCategory::class.java)
            .subscribeOn(Schedulers.boundedElastic()).subscribe()
        logger.info("Category with ID: ${categoryDeletedEvent.categoryId} deleted from Elasticsearch")
    }
}