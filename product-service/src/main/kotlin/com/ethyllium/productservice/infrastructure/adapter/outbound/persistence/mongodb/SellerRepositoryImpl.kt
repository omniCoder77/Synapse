package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.SellerRepository
import com.ethyllium.productservice.infrastructure.adapter.inbound.kafka.entities.SellerRegisteredEvent
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.SellerDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.TempSellerDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.toDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.util.scheduleDb
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SellerRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate) : SellerRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun insert(seller: Seller): Mono<Seller> {
        return reactiveMongoTemplate.insert(seller.toDocument()).map { it.toDomain() }
    }

    override fun update(
        sellerId: String,
        businessName: String?,
        displayName: String?,
        address: Address?,
        businessInfo: BusinessInfo?,
        sellerRating: SellerRating?,
        policies: SellerPolicies?,
        bankDetails: BankDetails?,
        taxInfo: TaxInfo?,
        status: SellerStatus?
    ): Mono<Boolean> {
        val update = Update()
        businessName?.let { update.set("businessName", it) }
        displayName?.let { update.set("displayName", it) }
        address?.let { update.set("address", it.toDocument()) }
        businessInfo?.let { update.set("businessInfo", it.toDocument()) }
        sellerRating?.let { update.set("sellerRating", it.toDocument()) }
        policies?.let { update.set("policies", it.toDocument()) }
        bankDetails?.let { update.set("bankDetails", it.toDocument()) }
        taxInfo?.let { update.set("taxInfo", it.toDocument()) }
        status?.let { update.set("status", it.name) }

        if (update.updateObject.isEmpty) {
            return Mono.just(false)
        }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)), update, SellerDocument::class.java
        ).map { it.modifiedCount > 0 }
    }

    override fun updatePhoneNumber(
        sellerId: String, phoneNumber: String
    ): Mono<Boolean> {
        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)),
            Update().set("phone", phoneNumber),
            SellerDocument::class.java
        ).map { it.modifiedCount > 0 }
    }

    override fun updateEmail(
        sellerId: String, email: String
    ): Mono<Boolean> {
        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)), Update().set("email", email), SellerDocument::class.java
        ).map { it.modifiedCount > 0 }
    }

    override fun delete(sellerId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("_id").`is`(sellerId)), SellerDocument::class.java
        ).map { it.deletedCount > 0 }
    }

    override fun findById(sellerId: String): Mono<Seller> {
        return reactiveMongoTemplate.findById(sellerId, SellerDocument::class.java).map { it.toDomain() }
    }

    override fun addTempSeller(sellerRegisteredEvent: SellerRegisteredEvent) {
        val tempSellerDocument = TempSellerDocument(
            sellerId = sellerRegisteredEvent.userId,
            email = sellerRegisteredEvent.email,
            role = sellerRegisteredEvent.role,
            phoneNumber = sellerRegisteredEvent.phoneNumber,
            name = sellerRegisteredEvent.name
        )
        reactiveMongoTemplate.save(tempSellerDocument).scheduleDb()
    }
}