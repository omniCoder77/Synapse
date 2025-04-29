package com.ethyllium.productservice.api

import com.ethyllium.productservice.dto.request.CreateProductRequest
import com.ethyllium.productservice.mapper.ProductMapper
import com.ethyllium.productservice.model.Product
import com.ethyllium.productservice.service.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productMapper: ProductMapper,
    private val productService: ProductService
) {

    @PostMapping
    fun add(@RequestBody createProductRequest: CreateProductRequest): Product {
        return productMapper.toProduct(createProductRequest)
    }

    @GetMapping("/{id}")
    fun doesProductExist(@PathVariable("id") id: String): Boolean {
        val product = productService.getProductById(id)
        return product != null
    }
}