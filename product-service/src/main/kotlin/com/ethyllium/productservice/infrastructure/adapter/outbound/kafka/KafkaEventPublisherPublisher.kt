package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.EventPublisher
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event.*
import com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.util.Topics
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.OutboxEventEntity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisherPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>, private val reactiveMongoTemplate: ReactiveMongoTemplate
) : EventPublisher {
    override fun publishBrandCreated(brand: Brand) {
        val event = BrandCreatedEvent(
            brand.id!!, brand.name, brand.website, brand.description, brand.logoUrl, brand.slug
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.BRAND_CREATED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishBrandUpdated(
        brandId: String,
        fileUrl: String?,
        description: String?,
        logoUrl: String?,
        website: String?,
        slug: String?,
        name: String?
    ) {
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
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(Topics.BRAND_UPDATED, kafkaEvent) }
    }

    override fun publishBrandDeleted(brandId: String) {
        val event = BrandDeletedEvent(
            brandId = brandId,
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.BRAND_DELETED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishCategoryCreated(category: Category) {
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
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishCategoryUpdated(
        categoryId: String, name: String?, description: String?, slug: String?, parentId: String?, imageUrl: String?
    ) {
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
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishCategoryDeleted(categoryId: String) {
        val event = CategoryDeletedEvent(categoryId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.CATEGORY_DELETED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishSellerCreated(seller: Seller) {
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
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
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
    ) {
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
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishSellerDeleted(sellerId: String) {
        val event = SellerDeletedEvent(sellerId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.SELLER_DELETED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishWarehouseStockCreated(warehouseStock: WarehouseStock) {
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
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishWarehouseStockUpdated(
        warehouseId: String, quantity: Int?, reservedQuantity: Int?, location: String?
    ) {
        val event = WarehouseStockUpdatedEvent(
            warehouseId = warehouseId, quantity = quantity, reservedQuantity = reservedQuantity, location = location
        )
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.WAREHOUSE_UPDATED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishWarehouseStockDeleted(warehouseId: String) {
        val event = WarehouseStockDeletedEvent(warehouseId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.WAREHOUSE_DELETED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishProductCreated(product: Product) {
        val event = product.toKafkaEvent()
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.PRODUCT_CREATED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishProductUpdated(product: Product) {
        val event = product.toKafkaEvent()
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.PRODUCT_UPDATED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }

    override fun publishProductDeleted(productId: String) {
        val event = ProductDeletedEvent(productId)
        val kafkaEvent = jacksonObjectMapper().writeValueAsString(event)
        val outboxEvent = OutboxEventEntity(
            eventTopic = Topics.PRODUCT_DELETED, payload = kafkaEvent
        )
        reactiveMongoTemplate.insert(outboxEvent).subscribe { kafkaTemplate.send(outboxEvent.eventTopic, kafkaEvent) }
    }
}