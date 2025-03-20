package com.ethyllium.searchservice.dto.request

data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val brand: String,
    val tags: List<String> = emptyList(),
    val rating: Double = 0.0,
    val attributes: Map<String, String> = mapOf()
)