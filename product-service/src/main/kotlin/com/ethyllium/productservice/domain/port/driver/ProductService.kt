package com.ethyllium.productservice.domain.port.driver

import com.ethyllium.productservice.domain.model.Product
import com.ethyllium.productservice.domain.model.ProductStatus
import com.ethyllium.productservice.domain.model.ProductVisibility
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.response.ProductResponse
import com.ethyllium.productservice.infrastructure.adapter.inbound.rest.rest.dto.request.UpdateProductRequest
import reactor.core.publisher.Mono

interface ProductService {

    fun createProduct(product: Product, sellerId: String): Mono<Product>
    fun createProductsBulk(requests: List<Product>, sellerId: String): Mono<String>

    fun updateProduct(id: String, request: UpdateProductRequest): Mono<ProductResponse>
    fun updateProductStatus(id: String, status: ProductStatus): Mono<String>
    fun updateProductVisibility(id: String, visibility: ProductVisibility): Mono<String>
    fun updateProductsStatusBulk(productIds: List<String>, status: ProductStatus, sellerId: String): Mono<String>

    fun deleteProduct(id: String): Mono<Void>
    fun deleteProductsBulk(productIds: List<String>): Mono<Void>
    fun archiveProduct(id: String): Mono<String>
    fun restoreProduct(id: String): Mono<String>

    fun isSellerOwner(sellerId: String, id: String): Mono<Boolean>
}