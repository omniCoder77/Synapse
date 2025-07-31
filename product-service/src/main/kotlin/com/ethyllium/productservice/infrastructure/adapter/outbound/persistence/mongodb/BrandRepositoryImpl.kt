// src/main/kotlin/com/ethyllium/productservice/infrastructure/adapter/outbound/persistence/mongodb/BrandRepositoryImpl.kt

package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb

import com.ethyllium.productservice.domain.exception.ProductDuplicateException
import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driven.BrandRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.BrandDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.toDocument
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BrandRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate) : BrandRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun insert(brand: Brand): Mono<Brand> {
        return reactiveMongoTemplate.insert(brand.toDocument()).onErrorMap(DuplicateKeyException::class.java) {
            logger.warn("Duplicate brand creation attempt for name: {}", brand.name)
            ProductDuplicateException("Brank with name '${brand.name}' already exists")
        }.map { insertedDocument -> insertedDocument.toDomain() }
    }

    override fun uploadLogo(brandId: String, fileUrl: String, ownerId: String): Mono<Boolean> {
        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(brandId).andOperator(Criteria.where("ownerId").`is`(ownerId))),
            Update.update("logo_url", fileUrl),
            BrandDocument::class.java
        ).map { it.modifiedCount > 0 } // Check if the document was actually modified
    }

    override fun update(
        brandId: String, name: String?, description: String?, website: String?, slug: String?, ownerId: String
    ): Mono<Boolean> {
        val update = Update()
        name?.let { update.set("name", it) }
        description?.let { update.set("description", it) }
        website?.let { update.set("website", it) }
        slug?.let { update.set("slug", it) }

        // If the update object is empty, no fields were provided for update. Return false.
        if (update.updateObject.isEmpty) {
            return Mono.just(false)
        }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(brandId).andOperator(Criteria.where("ownerId").`is`(ownerId))),
            update,
            BrandDocument::class.java
        ).map { result ->
            result.modifiedCount > 0 // Use modifiedCount to ensure a change occurred
        }
    }

    override fun delete(brandId: String, ownerId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("_id").`is`(brandId).andOperator(Criteria.where("ownerId").`is`(ownerId))),
            BrandDocument::class.java
        ).map { result ->
            result.deletedCount > 0 // Correctly checks for actual deletion
        }
    }
}