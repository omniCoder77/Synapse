package com.ethyllium.productservice.dto.request

import com.ethyllium.productservice.model.Review
import java.math.BigDecimal

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val discount: BigDecimal? = null,
    val sellerId: String,
    val attributes: Map<String, String> = mapOf(),
    val reviews: List<Review> = listOf(),
    val images: List<String> = listOf()
)