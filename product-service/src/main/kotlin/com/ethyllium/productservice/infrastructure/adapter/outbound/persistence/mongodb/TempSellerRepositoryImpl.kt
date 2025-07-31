package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb

import com.ethyllium.productservice.domain.port.driven.TempSellerRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.TempSellerDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class TempSellerRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate) : TempSellerRepository {

    override fun findById(sellerId: String): Mono<TempSellerDocument> {
        return reactiveMongoTemplate.findById(ObjectId(sellerId), TempSellerDocument::class.java)
    }

    override fun delete(sellerId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("sellerId").`is`(sellerId)), TempSellerDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun findBySellerId(sellerId: String): Mono<TempSellerDocument> {
        return reactiveMongoTemplate.findOne(
            Query.query(Criteria.where("sellerId").`is`(sellerId)), TempSellerDocument::class.java
        ).flatMap { Mono.just(it) }
    }
}