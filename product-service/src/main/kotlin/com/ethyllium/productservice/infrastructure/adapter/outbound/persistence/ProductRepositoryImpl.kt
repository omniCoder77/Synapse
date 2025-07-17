package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence

import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.domain.port.driven.ProductRepository
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.ProductDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.entity.toDomain
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Repository
class ProductRepositoryImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ProductRepository {

    override fun save(product: Product): Mono<Product> {
        return reactiveMongoTemplate.save(product.toDocument()).map { it.toDomain() }
    }

    override fun saveAll(products: List<Product>): Flux<Product> {
        return reactiveMongoTemplate.insertAll(products.map { it.toDocument() }).map { it.toDomain() }
    }

    override fun findById(id: String): Mono<Product> {
        val objectId = ObjectId(id)
        return reactiveMongoTemplate.findById(objectId, ProductDocument::class.java).map { it.toDomain() }
    }

    override fun existsBySku(sku: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("sku").`is`(sku))
        return reactiveMongoTemplate.exists(query, ProductDocument::class.java)
    }

    override fun existsBySkuAndIdNot(sku: String, id: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("sku").`is`(sku).and("_id").ne(ObjectId(id)))
        return reactiveMongoTemplate.exists(query, ProductDocument::class.java)
    }

    override fun findAllById(productIds: List<String>): Flux<Product> {
        val objectIds = productIds.map { ObjectId(it) }
        val query = Query.query(Criteria.where("_id").`in`(objectIds))
        return reactiveMongoTemplate.find(query, ProductDocument::class.java).map { it.toDomain() }
    }

    override fun updateStatus(id: String, status: ProductStatus): Mono<Long> {
        val query = Query.query(Criteria.where("_id").`is`(ObjectId(id)))
        val update = Update().set("productStatus", status.name).set("updatedAt", Instant.now())
        return reactiveMongoTemplate.updateFirst(query, update, ProductDocument::class.java)
            .map { it.modifiedCount }
    }

    override fun updateVisibility(id: String, visibility: ProductVisibility): Mono<Long> {
        val query = Query.query(Criteria.where("_id").`is`(ObjectId(id)))
        val update = Update().set("productVisibility", visibility.name).set("updatedAt", Instant.now())
        return reactiveMongoTemplate.updateFirst(query, update, ProductDocument::class.java)
            .map { it.modifiedCount }
    }

    override fun updateProductsStatusBulk(productIds: List<String>, status: ProductStatus, sellerId: String): Mono<Long> {
        val objectIds = productIds.map { ObjectId(it) }
        val query = Query.query(Criteria.where("_id").`in`(objectIds).and("sellerId").`is`(sellerId))
        val update = Update().set("productStatus", status.name).set("updatedAt", Instant.now())
        return reactiveMongoTemplate.updateMulti(query, update, ProductDocument::class.java)
            .map { it.modifiedCount }
    }

    override fun delete(id: String): Mono<Void> {
        val query = Query.query(Criteria.where("_id").`is`(ObjectId(id)))
        return reactiveMongoTemplate.remove(query, ProductDocument::class.java).then()
    }

    override fun deleteBulk(productIds: List<String>): Mono<Void> {
        val objectIds = productIds.map { ObjectId(it) }
        val query = Query.query(Criteria.where("_id").`in`(objectIds))
        return reactiveMongoTemplate.remove(query, ProductDocument::class.java).then()
    }

    override fun isSellerOwner(id: String, sellerId: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("_id").`is`(ObjectId(id)).and("sellerId").`is`(sellerId))
        return reactiveMongoTemplate.exists(query, ProductDocument::class.java)
    }
}