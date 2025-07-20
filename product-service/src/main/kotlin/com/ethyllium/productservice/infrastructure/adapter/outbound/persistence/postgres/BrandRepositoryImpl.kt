package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres

import com.ethyllium.productservice.domain.exception.ProductDuplicateException
import com.ethyllium.productservice.domain.model.Brand
import com.ethyllium.productservice.domain.port.driven.BrandRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.BrandDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity.toDocument
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

    override fun uploadLogo(brandId: String, fileUrl: String): Mono<Boolean> {
        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(brandId)),
            Update.update("logo_url", fileUrl),
            BrandDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun update(
        brandId: String, name: String?, description: String?, website: String?, slug: String?
    ): Mono<Boolean> {
        val update = Update()
        name?.let { update.set("name", it) }
        description?.let { update.set("description", it) }
        website?.let { update.set("website", it) }
        slug?.let { update.set("slug", it) }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(brandId)), update, BrandDocument::class.java
        ).map { result ->
            result.wasAcknowledged()
        }
    }

    override fun delete(brandId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("_id").`is`(brandId)), BrandDocument::class.java
        ).map { result ->
            result.wasAcknowledged()
        }
    }
}