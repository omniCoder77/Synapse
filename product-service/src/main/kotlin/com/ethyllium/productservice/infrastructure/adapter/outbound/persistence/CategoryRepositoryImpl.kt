package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence

import com.ethyllium.productservice.domain.exception.ProductDuplicateException
import com.ethyllium.productservice.domain.model.Category
import com.ethyllium.productservice.domain.port.driven.CategoryRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.CategoryDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toDomain
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CategoryRepositoryImpl(private val reactiveMongoTemplate: ReactiveMongoTemplate) : CategoryRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun insert(category: Category): Mono<Category> {
        return reactiveMongoTemplate.insert(category.toDocument())
            .onErrorMap(DuplicateKeyException::class.java) {
                logger.warn("Duplicate category creation attempt for slug: {}", category.slug)
                ProductDuplicateException("Category with slug '${category.slug}' already exists")
            }
            .map { it.toDomain() }
    }

    override fun update(
        categoryId: String,
        name: String?,
        description: String?,
        slug: String?,
        parentId: String?
    ): Mono<Boolean> {
        val update = Update()
        name?.let { update.set("name", it) }
        description?.let { update.set("description", it) }
        slug?.let { update.set("slug", it) }
        parentId?.let { update.set("parentId", it) }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("id").`is`(categoryId)),
            update,
            CategoryDocument::class.java
        ).map { it.wasAcknowledged() }
    }

    override fun delete(categoryId: String): Mono<Boolean> {
        return reactiveMongoTemplate.remove(
            Query.query(Criteria.where("id").`is`(categoryId)),
            CategoryDocument::class.java
        ).map { it.wasAcknowledged() }
    }
}