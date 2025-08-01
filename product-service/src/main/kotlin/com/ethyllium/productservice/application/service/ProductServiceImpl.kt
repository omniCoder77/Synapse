package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.application.util.CuckooFilterKey
import com.ethyllium.productservice.domain.exception.ProductDuplicateException
import com.ethyllium.productservice.domain.exception.ProductNotFoundException
import com.ethyllium.productservice.domain.exception.ProductValidationException
import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.domain.port.driven.CuckooFilter
import com.ethyllium.productservice.domain.port.driver.ProductService
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request.UpdateProductRequest
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ProductResponse
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.ProductDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.toDocument
import com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.mongodb.entity.toDomain
import com.mongodb.MongoWriteException
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant

@Service
class ProductServiceImpl(
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val cuckooFilter: CuckooFilter,
) : ProductService {

    private val logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        allEntries = true
    )
    override fun createProduct(product: Product, sellerId: String): Mono<Product> {
        return validateCreateRequest(product).then(reactiveMongoTemplate.insert(product.toDocument())).map {
            cuckooFilter.add(CuckooFilterKey.sku, product.sku).subscribeOn(Schedulers.boundedElastic()).subscribe()
            it.toDomain()
        }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        allEntries = true
    )
    override fun createProductsBulk(requests: List<Product>, sellerId: String): Mono<String> {
        if (requests.size > 100) {
            return Mono.error(ProductValidationException("Bulk creation limited to 100 products at once"))
        }
        return Mono.just(checkSkuDuplicates(requests.map { it.sku })).then(
            reactiveMongoTemplate.insertAll(requests.map { it.toDocument() })
                .then(Mono.just("Successfully created ${requests.size} products in bulk"))
                .onErrorMap(DuplicateKeyException::class.java) {
                    logger.warn("Duplicate key error during bulk creation")
                    ProductDuplicateException("One or more products already exist")
                })
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        key = "#id"
    )
    @CachePut(value = ["productsById"], key = "#id")
    override fun updateProduct(id: String, request: UpdateProductRequest, sellerId: String): Mono<ProductResponse> {
        logger.info("Updating product with ID: {}", id)

        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            return Mono.error(ProductValidationException("Invalid product ID format: $id"))
        }

        return reactiveMongoTemplate.findOne(
            Query.query(
                Criteria.where("_id").`is`(ObjectId(id))
            ).addCriteria(Criteria.where("sellerId").`is`(sellerId)), ProductDocument::class.java
        ).switchIfEmpty(
            Mono.error(ProductNotFoundException("Product not found with ID: $id"))
        ).flatMap { existingProduct ->
            validateUpdateRequest(request, existingProduct.sku).then(
                performUpdate(
                    objectId, request
                )
            )
        }.doOnSuccess { logger.info("Product updated successfully with ID: {}", id) }
            .onErrorMap(MongoWriteException::class.java) { ex ->
                when (ex.error.code) {
                    11000 -> {
                        logger.warn("Duplicate key error during product update for ID: {}", id)
                        ProductDuplicateException("Product update failed due to duplicate values")
                    }

                    else -> {
                        logger.error("MongoDB write error during product update for ID: {}", id, ex)
                        RuntimeException("Failed to update product", ex)
                    }
                }
            }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        key = "#id"
    )
    override fun updateProductStatus(id: String, status: ProductStatus): Mono<String> {
        logger.info("Updating product status to {} for ID: {}", status, id)

        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            return Mono.error(ProductValidationException("Invalid product ID format: $id"))
        }

        return reactiveMongoTemplate.findById(objectId, ProductDocument::class.java).switchIfEmpty(
            Mono.error(ProductNotFoundException("Product not found with ID: $id"))
        ).flatMap { existingProduct ->
            val update = Update().set(ProductDocument::productStatus.name, status.name)
                .set(ProductDocument::updatedAt.name, Instant.now())

            reactiveMongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").`is`(objectId)), update, ProductDocument::class.java
            ).map { updateResult ->
                if (updateResult.matchedCount == 0L) {
                    throw ProductNotFoundException("Product not found with ID: $id")
                }
                "Product status updated successfully for ID: $id"
            }
        }.doOnSuccess { logger.info("Product status updated successfully for ID: {}", id) }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        key = "#id"
    )
    override fun updateProductVisibility(id: String, visibility: ProductVisibility): Mono<String> {
        logger.info("Updating product visibility to {} for ID: {}", visibility, id)

        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            return Mono.error(ProductValidationException("Invalid product ID format: $id"))
        }

        return reactiveMongoTemplate.findById(objectId, ProductDocument::class.java).switchIfEmpty(
            Mono.error(ProductNotFoundException("Product not found with ID: $id"))
        ).flatMap { existingProduct ->
            val update = Update().set(ProductDocument::productVisibility.name, visibility.name)
                .set(ProductDocument::updatedAt.name, Instant.now())

            reactiveMongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").`is`(objectId)), update, ProductDocument::class.java
            ).map { updateResult ->
                if (updateResult.matchedCount == 0L) {
                    throw ProductNotFoundException("Product not found with ID: $id")
                }
                "Product visibility updated successfully for ID: $id"
            }
        }.doOnSuccess { logger.info("Product visibility updated successfully for ID: {}", id) }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        allEntries = true
    )
    override fun updateProductsStatusBulk(
        productIds: List<String>, status: ProductStatus, sellerId: String
    ): Mono<String> {
        logger.info("Updating status to {} for {} products", status, productIds.size)

        if (productIds.size > 100) {
            return Mono.error(ProductValidationException("Bulk status update limited to 100 products at once"))
        }

        val objectIds = try {
            productIds.map { ObjectId(it) }
        } catch (e: IllegalArgumentException) {
            return Mono.error(ProductValidationException("One or more invalid product ID formats"))
        }

        return reactiveMongoTemplate.find(
            Query.query(Criteria.where("_id").`in`(objectIds)), ProductDocument::class.java
        ).collectList().flatMap { products ->
            if (products.size != productIds.size) {
                val foundIds = products.map { it.id.toString() }
                val missingIds = productIds - foundIds.toSet()
                Mono.error(ProductNotFoundException("Products not found with IDs: ${missingIds.joinToString()}"))
            } else {
                val update = Update().set(ProductDocument::productStatus.name, status.name)
                    .set(ProductDocument::updatedAt.name, Instant.now())

                reactiveMongoTemplate.updateMulti(
                    Query.query(Criteria.where("_id").`in`(objectIds))
                        .addCriteria(Criteria.where(ProductDocument::sellerId.name).`is`(sellerId)),
                    update,
                    ProductDocument::class.java
                ).map { updateResult ->
                    logger.info("Successfully updated status for {} products", updateResult.modifiedCount)
                    "Product status updated successfully"
                }
            }
        }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        key = "#id"
    )
    override fun deleteProduct(id: String): Mono<Void> {
        logger.info("Deleting product with ID: {}", id)

        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            return Mono.error(ProductValidationException("Invalid product ID format: $id"))
        }

        return reactiveMongoTemplate.findById(objectId, ProductDocument::class.java).switchIfEmpty(
            Mono.error(ProductNotFoundException("Product not found with ID: $id"))
        ).flatMap { product ->
            cuckooFilter.remove(CuckooFilterKey.sku, product.sku).subscribeOn(Schedulers.boundedElastic()).subscribe()
            reactiveMongoTemplate.remove(
                Query.query(Criteria.where("_id").`is`(objectId)), ProductDocument::class.java
            )
        }.then().doOnSuccess { logger.info("Product deleted successfully with ID: {}", id) }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        allEntries = true
    )
    override fun deleteProductsBulk(productIds: List<String>): Mono<Void> {
        logger.info("Deleting {} products in bulk", productIds.size)

        if (productIds.size > 100) {
            return Mono.error(ProductValidationException("Bulk deletion limited to 100 products at once"))
        }

        val objectIds = try {
            productIds.map { ObjectId(it) }
        } catch (e: IllegalArgumentException) {
            return Mono.error(ProductValidationException("One or more invalid product ID formats"))
        }

        return reactiveMongoTemplate.find(
            Query.query(Criteria.where("_id").`in`(objectIds)), ProductDocument::class.java
        ).collectList().flatMap { existingProducts ->
            if (existingProducts.size != productIds.size) {
                val foundIds = existingProducts.map { it.id.toString() }
                val missingIds = productIds - foundIds.toSet()
                Mono.error(ProductNotFoundException("Products not found with IDs: ${missingIds.joinToString()}"))
            } else {
                reactiveMongoTemplate.remove(
                    Query.query(Criteria.where("_id").`in`(objectIds)), ProductDocument::class.java
                )
            }
        }.then().doOnSuccess { logger.info("Successfully deleted {} products", productIds.size) }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        key = "#id"
    )
    override fun archiveProduct(id: String): Mono<String> {
        return updateProductStatus(id, ProductStatus.ARCHIVED).map { "Product archived successfully for ID: $id" }
    }

    @CacheEvict(
        value = ["productsById", "productsBySku", "productsBySeller", "productsByCategory", "productsByBrand"],
        key = "#id"
    )
    override fun restoreProduct(id: String): Mono<String> {
        return updateProductStatus(id, ProductStatus.ACTIVE)
    }

    @Cacheable(value = ["sellerOwnership"], key = "#sellerId + '_' + #id")
    override fun isSellerOwner(sellerId: String, id: String): Mono<Boolean> {
        val objectId = try {
            ObjectId(id)
        } catch (e: IllegalArgumentException) {
            return Mono.just(false)
        }

        return reactiveMongoTemplate.exists(
            Query.query(
                Criteria.where("_id").`is`(objectId).and(ProductDocument::sellerId.name).`is`(sellerId)
            ), ProductDocument::class.java
        ).cache(Duration.ofMinutes(10))
    }

    private fun validateCreateRequest(request: Product): Mono<Boolean> {
        return cuckooFilter.exists(CuckooFilterKey.sku, request.sku)
    }

    private fun checkSkuDuplicates(skus: List<String>): Mono<Boolean> {
        val duplicateSkus = skus.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        if (duplicateSkus.isNotEmpty()) {
            return Mono.error(ProductValidationException("Duplicate SKUs in request: ${duplicateSkus.joinToString()}"))
        }

        return cuckooFilter.exists(CuckooFilterKey.sku, *skus.toTypedArray())
            .map { booleanList -> booleanList.all { it } }
    }

    private fun validateUpdateRequest(request: UpdateProductRequest, currentSku: String): Mono<Boolean> {
        return request.sku?.let {
            cuckooFilter.exists(CuckooFilterKey.sku, request.sku)
        } ?: Mono.just(true)
    }

    private fun performUpdate(
        objectId: ObjectId, request: UpdateProductRequest
    ): Mono<ProductResponse> {
        val update = Update().set(ProductDocument::updatedAt.name, Instant.now())

        request.name?.let { update.set(ProductDocument::name.name, it) }
        request.description?.let { update.set(ProductDocument::description.name, it) }
        request.shortDescription?.let { update.set(ProductDocument::shortDescription.name, it) }
        request.barcode?.let { update.set(ProductDocument::barcode.name, it) }
        request.brandId?.let { update.set(ProductDocument::brandId.name, it) }
        request.categoryId?.let { update.set(ProductDocument::categoryId.name, it) }

        request.pricing?.let { update.set(ProductDocument::pricing.name, it.toDocument()) }
        request.inventory?.let { update.set(ProductDocument::inventory.name, it.toDocument()) }
        request.specifications?.let { update.set(ProductDocument::specifications.name, it.toDocument()) }
        request.media?.let { update.set(ProductDocument::media.name, it.toDocument()) }
        request.seo?.let { update.set(ProductDocument::seo.name, it.toDocument()) }
        request.shipping?.let { update.set(ProductDocument::shipping.name, it.toDocument()) }

        request.tags?.let { update.set(ProductDocument::tags.name, it) }
        request.status?.let { update.set(ProductDocument::productStatus.name, it.name) }
        request.visibility?.let { update.set(ProductDocument::productVisibility.name, it.name) }
        request.sku?.let { update.set(ProductDocument::sku.name, it) }

        return reactiveMongoTemplate.updateFirst(
            Query.query(Criteria.where("_id").`is`(objectId)), update, ProductDocument::class.java
        ).flatMap { updateResult ->
            if (updateResult.matchedCount == 0L) {
                Mono.error(ProductNotFoundException("Product not found with ID: $objectId"))
            } else {
                reactiveMongoTemplate.findById(objectId, ProductDocument::class.java).map { it.toResponse() }
            }
        }
    }
}