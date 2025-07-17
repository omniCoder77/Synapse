package com.ethyllium.productservice.domain.port.driven

import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductRepository {

    /**
     * Saves a product entity. Use this for both creation and updates.
     * @param product The product to save.
     * @return The saved product.
     */
    fun save(product: Product): Mono<Product>

    /**
     * Inserts a collection of products. Optimized for bulk creation.
     * @param products The list of products to insert.
     * @return A list of the inserted products.
     */
    fun saveAll(products: List<Product>): Flux<Product>

    /**
     * Finds a product by its unique ID.
     * @param id The product ID.
     * @return A Mono emitting the found product or empty if not found.
     */
    fun findById(id: String): Mono<Product>

    /**
     * Checks if a product exists with the given SKU.
     * @param sku The product SKU.
     * @return A Mono emitting true if it exists, false otherwise.
     */
    fun existsBySku(sku: String): Mono<Boolean>

    /**
     * Checks if a product with a given SKU exists, excluding a product with a specific ID.
     * This is useful for validating SKU updates.
     * @param sku The SKU to check.
     * @param id The product ID to exclude from the check.
     * @return A Mono emitting true if a conflicting product exists, false otherwise.
     */
    fun existsBySkuAndIdNot(sku: String, id: String): Mono<Boolean>

    /**
     * Finds all products by their IDs.
     * @param productIds A list of product IDs.
     * @return A Flux emitting all found products.
     */
    fun findAllById(productIds: List<String>): Flux<Product>

    /**
     * Updates the status of a single product.
     * @param id The ID of the product to update.
     * @param status The new status.
     * @return A Mono emitting the number of modified documents (0 or 1).
     */
    fun updateStatus(id: String, status: ProductStatus): Mono<Long>

    /**
     * Updates the visibility of a single product.
     * @param id The ID of the product to update.
     * @param visibility The new visibility.
     * @return A Mono emitting the number of modified documents (0 or 1).
     */
    fun updateVisibility(id: String, visibility: ProductVisibility): Mono<Long>

    /**
     * Updates the status for multiple products owned by a specific seller.
     * @param productIds The IDs of the products to update.
     * @param status The new status.
     * @param sellerId The ID of the seller who owns the products.
     * @return A Mono emitting the number of modified documents.
     */
    fun updateProductsStatusBulk(productIds: List<String>, status: ProductStatus, sellerId: String): Mono<Long>

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     * @return A Mono that completes when the operation is done.
     */
    fun delete(id: String): Mono<Void>

    /**
     * Deletes multiple products by their IDs.
     * @param productIds The list of IDs of the products to delete.
     * @return A Mono that completes when the operation is done.
     */
    fun deleteBulk(productIds: List<String>): Mono<Void>

    /**
     * Checks if a product is owned by a specific seller.
     * @param id The product ID.
     * @param sellerId The seller ID.
     * @return A Mono emitting true if the seller owns the product, false otherwise.
     */
    fun isSellerOwner(id: String, sellerId: String): Mono<Boolean>
}