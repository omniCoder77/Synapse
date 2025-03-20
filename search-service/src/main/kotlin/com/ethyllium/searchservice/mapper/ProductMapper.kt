package com.ethyllium.searchservice.mapper

import com.ethyllium.searchservice.dto.request.ProductRequest
import com.ethyllium.searchservice.model.Product
import org.springframework.stereotype.Component

@Component
class ProductMapper {

    fun createProduct(product: ProductRequest) = Product(
        name = product.name,
        category = product.category,
        brand = product.brand,
        tags = product.tags,
        rating = product.rating,
        attributes = product.attributes,
        description = product.description,
        price = product.price
    )
}