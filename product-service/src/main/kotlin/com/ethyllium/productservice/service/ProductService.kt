package com.ethyllium.productservice.service

import com.ethyllium.productservice.model.Product
import com.ethyllium.productservice.ports.GetProduct
import com.ethyllium.productservice.repository.ProductRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class ProductService(private val productRepository: ProductRepository) : GetProduct {


    @Cacheable(value = ["defaultCache"], key = "#id")
    override fun getProductById(id: String): Product? {
        return productRepository.findById(id).getOrNull()
    }
}