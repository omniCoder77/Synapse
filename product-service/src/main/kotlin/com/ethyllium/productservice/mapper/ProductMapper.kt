package com.ethyllium.productservice.mapper

import com.ethyllium.productservice.dto.request.CreateProductRequest
import com.ethyllium.productservice.exception.SellerNotFoundException
import com.ethyllium.productservice.model.Product
import com.ethyllium.productservice.ports.SellerExists
import org.springframework.stereotype.Component

@Component
class ProductMapper(private val sellerExists: SellerExists) {
    fun toProduct(createProductRequest: CreateProductRequest): Product {
        val sellerExists = sellerExists.sellerExists(createProductRequest.sellerId)
        if (sellerExists) {
            throw SellerNotFoundException(createProductRequest.sellerId)
        }
        return Product(
            name = createProductRequest.name,
            discount = createProductRequest.discount,
            description = createProductRequest.description,
            attributes = createProductRequest.attributes,
            reviews = createProductRequest.reviews,
            images = createProductRequest.images,
            price = createProductRequest.price,
            sellerId = createProductRequest.sellerId,
        )
    }}