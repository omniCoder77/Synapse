package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.OutboxEntityRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.util.Topics
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.OutboxEventEntity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OutboxEntityRepositoryImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : OutboxEntityRepository {
    override fun publishBrandCreated(brand: Brand): Mono<OutboxEventEntity> {
        val event = BrandCreatedEvent(
            brand.id!!, brand.name, brand.website, brand.description, brand.logoUrl, brand.slug
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.BRAND_CREATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent)
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
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.BRAND_UPDATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishBrandDeleted(brandId: String): Mono<Void> {
        val event = BrandDeletedEvent(
            brandId = brandId,
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.BRAND_DELETED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
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
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.CATEGORY_CREATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishCategoryUpdated(
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?, imageUrl: String?
    ): Mono<Void> {
        val event = CategoryUpdatedEvent(
            categoryId = categoryId,
            name = name,
            description = description,
            slug = slug,
            parentId = parentId,
            imageUrl = imageUrl
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.CATEGORY_UPDATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishCategoryDeleted(categoryId: String): Mono<Void> {
        val event = CategoryDeletedEvent(categoryId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.CATEGORY_DELETED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
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
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.SELLER_CREATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishSellerUpdated(
        sellerId: String,
        businessName: String?,
        displayName: String?,
        address: Address?,
        businessInfo: BusinessInfo?,
        sellerRating: SellerRating?,
        policies: SellerPolicies?,
        bankDetails: BankDetails?,
        taxInfo: TaxInfo?
    ): Mono<Void> {
        val event = SellerUpdatedEvent(
            sellerId = sellerId,
            businessName = businessName,
            displayName = displayName,
            street = address?.street,
            city = address?.city,
            state = address?.state,
            postalCode = address?.postalCode,
            country = address?.country,
            coordinates = address?.coordinates,
            businessType = businessInfo?.businessType,
            registrationNumber = businessInfo?.registrationNumber,
            taxId = businessInfo?.taxId,
            website = businessInfo?.website,
            description = businessInfo?.description,
            yearEstablished = businessInfo?.yearEstablished,
            employeeCount = businessInfo?.employeeCount,
            averageRating = sellerRating?.averageRating ?: 0L,
            totalRatings = sellerRating?.totalRatings ?: 0,
            ratingDistribution = sellerRating?.ratingDistribution ?: emptyMap(),
            badges = sellerRating?.badges ?: emptyList(),
            returnPolicy = policies?.returnPolicy,
            shippingPolicy = policies?.shippingPolicy,
            privacyPolicy = policies?.privacyPolicy,
            termsOfService = policies?.termsOfService,
            warrantyPolicy = policies?.warrantyPolicy,
            bankName = bankDetails?.bankName,
            accountNumber = bankDetails?.accountNumber,
            accountHolderName = bankDetails?.accountHolderName,
            routingNumber = bankDetails?.routingNumber,
            accountType = bankDetails?.accountType,
            vatNumber = taxInfo?.vatNumber,
            taxExempt = taxInfo?.taxExempt,
            taxJurisdictions = taxInfo?.taxJurisdictions
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.SELLER_UPDATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishSellerDeleted(sellerId: String): Mono<Void> {
        val event = SellerDeletedEvent(sellerId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.SELLER_DELETED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishWarehouseStockCreated(warehouseStock: WarehouseStock): Mono<Void> {
        val event = WarehouseStockCreatedEvent(
            warehouseId = warehouseStock.warehouseId,
            warehouseName = warehouseStock.warehouseName,
            quantity = warehouseStock.quantity,
            reservedQuantity = warehouseStock.reservedQuantity,
            location = warehouseStock.location
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.WAREHOUSE_CREATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishWarehouseStockUpdated(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ): Mono<Void> {
        val event = WarehouseStockUpdatedEvent(
            warehouseId = warehouseId, quantity = quantity, reservedQuantity = reservedQuantity, location = location
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.WAREHOUSE_UPDATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishWarehouseStockDeleted(warehouseId: String): Mono<Void> {
        val event = WarehouseStockDeletedEvent(warehouseId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.WAREHOUSE_DELETED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishProductCreated(product: Product): Mono<Void> {
        val event = product.toKafkaEvent()
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.PRODUCT_CREATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishProductUpdated(product: Product): Mono<Void> {
        val event = product.toKafkaEvent()
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.PRODUCT_UPDATED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }

    override fun publishProductDeleted(productId: String): Mono<Void> {
        val event = ProductDeletedEvent(productId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.PRODUCT_DELETED, payload = kafkaEvent
        )
        return reactiveMongoTemplate.insert(outboxEvent).then()
    }
}