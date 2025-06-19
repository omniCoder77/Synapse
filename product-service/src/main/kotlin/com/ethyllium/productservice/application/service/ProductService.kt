package com.ethyllium.productservice.application.service

import com.ethyllium.productservice.domain.entity.ProductStatus
import com.ethyllium.productservice.domain.entity.ProductVisibility
import com.ethyllium.productservice.infrastructure.web.rest.dto.request.CreateProductRequest
import com.ethyllium.productservice.infrastructure.web.rest.dto.request.UpdateProductRequest
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ProductResponse
import com.ethyllium.productservice.infrastructure.web.rest.dto.response.ProductSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductService {

    fun createProduct(request: CreateProductRequest, sellerId: String): String
    fun createProductsBulk(requests: List<CreateProductRequest>, sellerId: String,): String

    fun getProductById(id: String): ProductResponse
    fun getProductBySku(sku: String): ProductResponse
    fun getProductsBySeller(sellerId: String, pageable: Pageable, status: ProductStatus?): Page<ProductSummaryResponse>
    fun getProductsByCategory(categoryId: String, pageable: Pageable): Page<ProductSummaryResponse>
    fun getProductsByBrand(brandId: String, pageable: Pageable): Page<ProductSummaryResponse>

    fun updateProduct(id: String, request: UpdateProductRequest): ProductResponse
    fun updateProductStatus(id: String, status: ProductStatus): String
    fun updateProductVisibility(id: String, visibility: ProductVisibility): String
    fun updateProductsStatusBulk(productIds: List<String>, status: ProductStatus): String

    fun deleteProduct(id: String)
    fun deleteProductsBulk(productIds: List<String>)
    fun archiveProduct(id: String): String
    fun restoreProduct(id: String): String

    fun existsById(id: String): Boolean
    fun isSellerOwner(sellerId: String, id: String): Boolean
}