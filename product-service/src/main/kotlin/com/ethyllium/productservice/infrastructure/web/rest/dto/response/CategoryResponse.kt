package com.ethyllium.productservice.infrastructure.web.rest.dto.response

data class CategoryResponse(
    val id: String,
    val name: String,
    val description: String?,
    val parentId: String?,
    val slug: String,
    val level: Int,
    val path: String,
    val imageUrl: String?
)