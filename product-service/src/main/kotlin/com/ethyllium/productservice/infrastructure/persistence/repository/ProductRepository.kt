package com.ethyllium.productservice.infrastructure.persistence.repository

import com.ethyllium.productservice.infrastructure.persistence.entity.ProductDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProductRepository : MongoRepository<ProductDocument, String> {

    fun findBySku(sku: String): Optional<ProductDocument>

    fun findBySellerIdAndProductStatus(sellerId: String, status: String, pageable: Pageable): Page<ProductDocument>

    fun findBySellerId(sellerId: String, pageable: Pageable): Page<ProductDocument>

    fun findByCategoryName(categoryId: String, pageable: Pageable): Page<ProductDocument>

    fun findByBrandName(brandId: String, pageable: Pageable): Page<ProductDocument>

    fun existsBySku(sku: String): Boolean
}