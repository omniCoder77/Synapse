package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence

import com.ethyllium.productservice.domain.model.Seller
import com.ethyllium.productservice.domain.port.driven.SellerRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.SellerDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toDocument
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

    override fun update(sellerId: String, businessName: String?, displayName: String?, phone: String?): Mono<Boolean> {
        val update = Update()
        businessName?.let { update.set("businessName", it) }
        displayName?.let { update.set("displayName", it) }
        phone?.let { update.set("phone", it) }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(sellerId)), update, SellerDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun delete(sellerId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("_id").`is`(sellerId)), SellerDocument::class.java
        ).map { it.wasAcknowledged() }
    }
}