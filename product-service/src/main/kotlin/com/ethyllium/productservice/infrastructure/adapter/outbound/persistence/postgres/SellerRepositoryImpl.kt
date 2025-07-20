package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres

import com.ethyllium.productservice.domain.model.*
import com.ethyllium.productservice.domain.port.driven.SellerRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.SellerDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.toDocument
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SellerRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate) : SellerRepository {

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
        address?.let { update.set("phone", it) }
        businessInfo?.let { update.set("businessInfo", it) }
        sellerRating?.let { update.set("sellerRating", it) }
        policies?.let { update.set("policies", it) }
        bankDetails?.let { update.set("bankDetails", it) }
        taxInfo?.let { update.set("taxInfo", it) }
        status?.let { update.set("status", it.name) }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)), update, SellerDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun updatePhoneNumber(
        sellerId: String, phoneNumber: String
    ): Mono<Boolean> {
        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)),
            Update().set("phone", phoneNumber),
            SellerDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun updateEmail(
        sellerId: String,
        email: String
    ): Mono<Boolean> {
                return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)),
            Update().set("email", email),
            SellerDocument::class.java
        ).map { it.wasAcknowledged() }

    }

    override fun delete(sellerId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("_id").`is`(sellerId)), SellerDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun findById(sellerId: String): Mono<Seller> {
        return reactiveMongoTemplate.findById(sellerId, SellerDocument::class.java).map { it.toDomain() }
    }
}