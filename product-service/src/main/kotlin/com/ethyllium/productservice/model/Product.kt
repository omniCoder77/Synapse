package com.ethyllium.productservice.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document
data class Product(
    @field:NotBlank(message = "Product name cannot be blank") @field:Size(
        min = 2, max = 100, message = "Product name must be between 2 and 100 characters"
    ) @Indexed(unique = false) val name: String,

    @field:NotBlank(message = "Description cannot be blank") @field:Size(
        max = 1000, message = "Description must be less than 1000 characters"
    ) val description: String,

    @field:Min(value = 0, message = "Price must be non-negative") val price: BigDecimal,

    val discount: BigDecimal? = null,

    @field:NotBlank(message = "Seller ID cannot be blank") val sellerId: String,

    val attributes: Map<String, String> = mapOf(),

    val reviews: List<Review> = listOf(),

    val images: List<String> = listOf()
)