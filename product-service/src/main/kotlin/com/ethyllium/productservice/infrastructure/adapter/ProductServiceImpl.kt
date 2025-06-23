package com.ethyllium.productservice.infrastructure.adapter

import com.ethyllium.productservice.application.service.ProductService
import com.ethyllium.productservice.domain.entity.ProductStatus
import com.ethyllium.productservice.domain.entity.ProductVisibility
import com.ethyllium.productservice.domain.exception.ProductDuplicateException
import com.ethyllium.productservice.domain.exception.ProductNotFoundException
import com.ethyllium.productservice.domain.exception.ProductValidationException
import com.ethyllium.productservice.infrastructure.persistence.entity.ProductDocument
import com.ethyllium.productservice.infrastructure.persistence.repository.ProductRepository
import com.ethyllium.productservice.infrastructure.web.rest.dto.request.CreateProductRequest
import com.ethyllium.productservice.infrastructure.web.rest.dto.request.UpdateProductRequest
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ProductResponse
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ProductSummaryResponse
import com.mongodb.MongoWriteException
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository, private val mongoTemplate: MongoTemplate
) : ProductService {

    private val logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    @Transactional
    override fun createProduct(request: CreateProductRequest, sellerId: String): String {
        validateCreateRequest(request)
        try {
            val productDocument = request.toDocument(sellerId)
            val savedProduct = productRepository.save(productDocument)

            logger.info("Product created successfully with ID: {}", savedProduct.id)
            return savedProduct.id!!.toString()
        } catch (_: DuplicateKeyException) {
            logger.warn("Duplicate product creation attempt for SKU: {}", request.sku)
            throw ProductDuplicateException("Product with SKU '${request.sku}' already exists")
        }
    }

    @Transactional
    override fun createProductsBulk(requests: List<CreateProductRequest>, sellerId: String): String {
        if (requests.size > 100) {
            throw ProductValidationException("Bulk creation limited to 100 products at once")
        }

        requests.forEach { validateCreateRequest(it) }

        val skus = requests.map { it.sku }
        val duplicateSkus = skus.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        if (duplicateSkus.isNotEmpty()) {
            throw ProductValidationException("Duplicate SKUs in request: ${duplicateSkus.joinToString()}")
        }

        val existingSkus = productRepository.findAll().filter { skus.contains(it.sku) }.map { it.sku }

        if (existingSkus.isNotEmpty()) {
            throw ProductDuplicateException("Products with following SKUs already exist: ${existingSkus.joinToString()}")
        }
        try {
            val productDocuments = requests.map { it.toDocument(sellerId) }
            val savedProducts = productRepository.saveAll(productDocuments)
            return "Successfully create ${savedProducts.size} products in bulk"
        } catch (_: DuplicateKeyException) {
            logger.warn("Duplicate key error during bulk creation")
            throw ProductDuplicateException("One or more products already exist")
        }
    }

    @Transactional(readOnly = true)
    override fun getProductById(id: String): ProductResponse {
        logger.debug("Fetching product by ID: {}", id)

        val product = productRepository.findById(id).orElseThrow {
            logger.warn("Product not found with ID: {}", id)
            ProductNotFoundException("Product not found with ID: $id")
        }

        return product.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getProductBySku(sku: String): ProductResponse {
        logger.debug("Fetching product by SKU: {}", sku)

        val product = productRepository.findBySku(sku).orElseThrow {
            logger.warn("Product not found with SKU: {}", sku)
            ProductNotFoundException("Product not found with SKU: $sku")
        }

        return product.toResponse()
    }

    @Transactional(readOnly = true)
    override fun getProductsBySeller(
        sellerId: String, pageable: Pageable, status: ProductStatus?
    ): Page<ProductSummaryResponse> {
        logger.debug("Fetching products for seller: {} with status: {}", sellerId, status)

        val products = if (status != null) {
            productRepository.findBySellerIdAndProductStatus(sellerId, status.name, pageable)
        } else {
            productRepository.findBySellerId(sellerId, pageable)
        }

        return products.map { it.toSummaryResponse() }
    }

    @Transactional(readOnly = true)
    override fun getProductsByCategory(categoryId: String, pageable: Pageable): Page<ProductSummaryResponse> {
        logger.debug("Fetching products for category: {}", categoryId)

        val products = productRepository.findByCategoryName(categoryId, pageable)
        return products.map { it.toSummaryResponse() }
    }

    @Transactional(readOnly = true)
    override fun getProductsByBrand(brandId: String, pageable: Pageable): Page<ProductSummaryResponse> {
        logger.debug("Fetching products for brand: {}", brandId)

        val products = productRepository.findByBrandName(brandId, pageable)
        return products.map { it.toSummaryResponse() }
    }

    @Transactional
    override fun updateProduct(id: String, request: UpdateProductRequest): ProductResponse {
        logger.info("Updating product with ID: {}", id)

        // Find existing product
        val query = Query.query(Criteria.where("id").`is`(id))
        val existingProduct = mongoTemplate.findOne(query, ProductDocument::class.java) ?: run {
            logger.warn("Product not found for update with ID: {}", id)
            throw ProductNotFoundException("Product not found with ID: $id")
        }

        validateUpdateRequest(request, existingProduct.sku)

        try {
            val updateQuery = Query.query(Criteria.where("id").`is`(id))
            val update = Update()

            request.name?.let { update.set("name", it) }
            request.description?.let { update.set("description", it) }
            request.shortDescription?.let { update.set("shortDescription", it) }
            request.barcode?.let { update.set("barcode", it) }
            request.brandId?.let { update.set("brandId", it) }
            request.categoryId?.let { update.set("categoryId", it) }
            request.pricing?.let { update.set("pricing", it) }
            request.inventory?.let { update.set("inventory", it) }
            request.specifications?.let { update.set("specifications", it) }
            request.media?.let { update.set("media", it) }
            request.seo?.let { update.set("seo", it) }
            request.shipping?.let { update.set("shipping", it) }
            request.tags?.let { update.set("tags", it) }
            request.status?.let { update.set("status", it) }
            request.visibility?.let { update.set("visibility", it) }
            request.sku?.let { update.set("sku", it) }

            update.set("updatedAt", LocalDateTime.now())

            val updateResult = mongoTemplate.updateFirst(updateQuery, update, ProductDocument::class.java)

            if (updateResult.matchedCount == 0L) {
                logger.warn("No product found to update with ID: {}", id)
                throw ProductNotFoundException("Product not found with ID: $id")
            }

            val updatedProduct = mongoTemplate.findOne(query, ProductDocument::class.java)
                ?: throw RuntimeException("Failed to retrieve updated product")

            logger.info("Product updated successfully with ID: {}", updatedProduct.id)
            return updatedProduct.toResponse()

        } catch (ex: MongoWriteException) {
            when (ex.error.code) {
                11000 -> {
                    logger.warn("Duplicate key error during product update for ID: {}", id)
                    throw ProductDuplicateException("Product update failed due to duplicate values")
                }

                else -> {
                    logger.error("MongoDB write error during product update for ID: {}", id, ex)
                    throw RuntimeException("Failed to update product", ex)
                }
            }
        } catch (ex: DuplicateKeyException) {
            logger.warn("Duplicate key error during product update for ID: {}", id)
            throw ProductDuplicateException("Product update failed due to duplicate values")
        }
    }

    @Transactional
    override fun updateProductStatus(id: String, status: ProductStatus): String {
        logger.info("Updating product status to {} for ID: {}", status, id)

        val existingProduct = productRepository.findById(id).orElseThrow {
            logger.warn("Product not found for status update with ID: {}", id)
            ProductNotFoundException("Product not found with ID: $id")
        }

        val updatedProduct = existingProduct.copy(
            productStatus = status.name, updatedAt = Instant.now()
        )

        val savedProduct = productRepository.save(updatedProduct)

        logger.info("Product status updated successfully for ID: {}", savedProduct.id)
        return "Product status updated successfully for ID: $id"
    }

    @Transactional
    override fun updateProductVisibility(id: String, visibility: ProductVisibility): String {
        logger.info("Updating product visibility to {} for ID: {}", visibility, id)

        val existingProduct = productRepository.findById(id).orElseThrow {
            logger.warn("Product not found for visibility update with ID: {}", id)
            ProductNotFoundException("Product not found with ID: $id")
        }

        val updatedProduct = existingProduct.copy(
            productVisibility = visibility.name, updatedAt = Instant.now()
        )

        val savedProduct = productRepository.save(updatedProduct)

        logger.info("Product visibility updated successfully for ID: {}", savedProduct.id)
        return "Product visibility updated successfully for ID: $id"
    }

    @Transactional
    override fun updateProductsStatusBulk(productIds: List<String>, status: ProductStatus): String {
        logger.info("Updating status to {} for {} products", status, productIds.size)

        if (productIds.size > 100) {
            throw ProductValidationException("Bulk status update limited to 100 products at once")
        }

        val products = productRepository.findAllById(productIds)
        if (products.size != productIds.size) {
            val foundIds = products.map { it.id }
            val missingIds = productIds - foundIds.toSet()
            throw ProductNotFoundException("Products not found with IDs: ${missingIds.joinToString()}")
        }

        val updatedProducts = products.map { product ->
            product.copy(
                productStatus = status.name, updatedAt = Instant.now()
            )
        }

        val savedProducts = productRepository.saveAll(updatedProducts)

        logger.info("Successfully updated status for {} products", savedProducts.size)
        return "Product status updated successfully"
    }

    @Transactional
    override fun deleteProduct(id: String) {
        logger.info("Deleting product with ID: {}", id)

        if (!productRepository.existsById(id)) {
            logger.warn("Product not found for deletion with ID: {}", id)
            throw ProductNotFoundException("Product not found with ID: $id")
        }

        productRepository.deleteById(id)

        logger.info("Product deleted successfully with ID: {}", id)
    }

    @Transactional
    override fun deleteProductsBulk(productIds: List<String>) {
        logger.info("Deleting {} products in bulk", productIds.size)

        if (productIds.size > 100) {
            throw ProductValidationException("Bulk deletion limited to 100 products at once")
        }

        val existingProducts = productRepository.findAllById(productIds)
        if (existingProducts.size != productIds.size) {
            val foundIds = existingProducts.map { it.id }
            val missingIds = productIds - foundIds.toSet()
            throw ProductNotFoundException("Products not found with IDs: ${missingIds.joinToString()}")
        }

        productRepository.deleteAllById(productIds)

        logger.info("Successfully deleted {} products", productIds.size)
    }

    @Transactional
    override fun archiveProduct(id: String): String {
        updateProductStatus(id, ProductStatus.ARCHIVED)
        return "Product archived successfully for ID: $id"
    }

    @Transactional
    override fun restoreProduct(id: String): String {
        updateProductStatus(id, ProductStatus.ACTIVE)
        return "Product status updated successfully for ID: $id"
    }

    @Transactional(readOnly = true)
    override fun existsById(id: String): Boolean {
        return productRepository.existsById(id)
    }

    override fun isSellerOwner(sellerId: String, id: String): Boolean {
        val product = productRepository.findById(id).orElse(null) ?: return false
        return product.sellerId == sellerId
    }

    private fun validateCreateRequest(request: CreateProductRequest) {
        if (productRepository.existsBySku(request.sku)) {
            throw ProductDuplicateException("Product with SKU '${request.sku}' already exists")
        }

        if (request.pricing.salePrice != null && request.pricing.salePrice >= request.pricing.basePrice) {
            throw ProductValidationException("Sale price must be less than base price")
        }

        if (request.inventory.reservedQuantity > request.inventory.stockQuantity) {
            throw ProductValidationException("Reserved quantity cannot exceed stock quantity")
        }
    }

    private fun validateUpdateRequest(request: UpdateProductRequest, currentSku: String) {
        // Check for SKU conflicts if SKU is being updated
        request.sku?.let { newSku ->
            if (newSku != currentSku && productRepository.existsBySku(newSku)) {
                throw ProductDuplicateException("Product with SKU '$newSku' already exists")
            }
        }

        // Additional business validations
        request.pricing?.let { pricing ->
            if (pricing.salePrice != null && pricing.salePrice >= pricing.basePrice) {
                throw ProductValidationException("Sale price must be less than base price")
            }
        }

        request.inventory?.let { inventory ->
            if (inventory.reservedQuantity > inventory.stockQuantity) {
                throw ProductValidationException("Reserved quantity cannot exceed stock quantity")
            }
        }
    }
}