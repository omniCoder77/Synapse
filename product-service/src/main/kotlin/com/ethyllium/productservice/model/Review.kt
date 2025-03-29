package com.ethyllium.productservice.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Review(
    val review: String = "",
    val description: String = "",
    val rating: Int = 0,
    val userName: String = "",
    val images: List<String> = listOf(),
)