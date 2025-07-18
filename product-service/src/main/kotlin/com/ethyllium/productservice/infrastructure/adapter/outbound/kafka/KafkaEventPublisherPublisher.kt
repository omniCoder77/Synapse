package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.util.Topics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class KafkaEventPublisherPublisher(private val kafkaTemplate: KafkaTemplate<String, Event>) : EventPublisher {
    override fun publishBrandCreated(brand: Brand): Mono<Void> {
        val event = BrandCreatedEvent(
            brand.id!!, brand.name, brand.website, brand.description, brand.logoUrl, brand.slug
        )
        return Mono.just(kafkaTemplate.send(Topics.BRAND_CREATED, event)).then()
    }

    override fun publishBrandUpdated(
        brandId: String,
        fileUrl: String?,
        description: String?,
        logoUrl: String?,
        website: String?,
        slug: String?,
        name: String?
    ): Mono<Void> {
        val event = BrandUpdatedEvent(
            brandId = brandId,
            fileUrl = fileUrl,
            description = description,
            logoUrl = logoUrl,
            website = website,
            slug = slug,
            name = name
        )
        return Mono.just(kafkaTemplate.send(Topics.BRAND_UPDATED, event)).then()
    }

    override fun publishBrandDeleted(brandId: String): Mono<Void> {
        val event = BrandDeletedEvent(
            brandId = brandId,
        )
        return Mono.just(kafkaTemplate.send(Topics.BRAND_DELETED, event)).then()
    }

    override fun publishCategoryCreated(category: Category): Mono<Void> {
        val event = CategoryCreatedEvent(
            id = category.id!!,
            name = category.name,
            description = category.description,
            parentId = category.parentId,
            slug = category.slug,
            level = category.level,
            path = category.path,
            imageUrl = category.imageUrl
        )
        return Mono.just(kafkaTemplate.send(Topics.CATEGORY_CREATED, event)).then()
    }

    override fun publishCategoryUpdated(
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?
    ): Mono<Void> {
        val event = CategoryUpdatedEvent(
            categoryId = categoryId, name = name, description = description, slug = slug, parentId = parentId
        )
        return Mono.just(kafkaTemplate.send(Topics.CATEGORY_UPDATED, event)).then()
    }

    override fun publishCategoryDeleted(categoryId: String): Mono<Void> {
        val event = CategoryDeletedEvent(categoryId)
        return Mono.just(kafkaTemplate.send(Topics.CATEGORY_DELETED, event)).then()
    }

    override fun publishSellerCreated(seller: Seller): Mono<Void> {
        val event = SellerCreatedEvent(
            id = seller.id,
            businessName = seller.businessName,
            displayName = seller.displayName,
            email = seller.email,
            phone = seller.phone,
            address = seller.address,
            businessInfo = seller.businessInfo,
            sellerRating = seller.sellerRating,
            policies = seller.policies,
            bankDetails = seller.bankDetails,
            taxInfo = seller.taxInfo
        )
        return Mono.just(kafkaTemplate.send(Topics.SELLER_CREATED, event)).then()
    }

    override fun publishSellerUpdated(
        sellerId: String, businessName: String?, displayName: String?, phone: String?
    ): Mono<Void> {
        val event = SellerUpdatedEvent(
            sellerId = sellerId, businessName = businessName, displayName = displayName, phone = phone
        )
        return Mono.just(kafkaTemplate.send(Topics.SELLER_UPDATED, event)).then()
    }

    override fun publishSellerDeleted(sellerId: String): Mono<Void> {
        val event = SellerDeletedEvent(sellerId)
        return Mono.just(kafkaTemplate.send(Topics.SELLER_DELETED, event)).then()
    }

    override fun publishWarehouseStockCreated(warehouseStock: WarehouseStock): Mono<Void> {
        val event = WarehouseStockCreatedEvent(
            warehouseId = warehouseStock.warehouseId,
            warehouseName = warehouseStock.warehouseName,
            quantity = warehouseStock.quantity,
            reservedQuantity = warehouseStock.reservedQuantity,
            location = warehouseStock.location
        )
        return Mono.just(kafkaTemplate.send(Topics.WAREHOUSE_CREATED, event)).then()
    }

    override fun publishWarehouseStockUpdated(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ): Mono<Void> {
        val event = WarehouseStockUpdatedEvent(
            warehouseId = warehouseId, quantity = quantity, reservedQuantity = reservedQuantity, location = location
        )
        return Mono.just(kafkaTemplate.send(Topics.WAREHOUSE_UPDATED, event)).then()
    }

    override fun publishWarehouseStockDeleted(warehouseId: String): Mono<Void> {
        val event = WarehouseStockDeletedEvent(warehouseId)
        return Mono.just(kafkaTemplate.send(Topics.WAREHOUSE_DELETED, event)).then()
    }

    override fun publishProductCreated(product: Product): Mono<Void> {
        val event = product.toCreatedKafkaEvent()
        return Mono.just(kafkaTemplate.send(Topics.PRODUCT_CREATED, event)).then()
    }

    override fun publishProductUpdated(product: Product): Mono<Void> {
        val event = product.toCreatedKafkaEvent()
        return Mono.just(kafkaTemplate.send(Topics.PRODUCT_UPDATED, event)).then()
    }

    override fun publishProductDeleted(productId: String): Mono<Void> {
        val event = ProductDeletedEvent(productId)
        return Mono.just(kafkaTemplate.send(Topics.PRODUCT_DELETED, event)).then()
    }
}