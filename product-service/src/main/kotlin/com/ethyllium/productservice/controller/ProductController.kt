package com.ethyllium.productservice.controller

import com.ethyllium.productservice.dto.request.CreateProductRequest
import com.ethyllium.productservice.mapper.ProductMapper
import com.ethyllium.productservice.model.Product
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productMapper: ProductMapper
) {

    @PostMapping
    fun add(@RequestBody createProductRequest: CreateProductRequest): Product {
        return productMapper.toProduct(createProductRequest)
    }
}