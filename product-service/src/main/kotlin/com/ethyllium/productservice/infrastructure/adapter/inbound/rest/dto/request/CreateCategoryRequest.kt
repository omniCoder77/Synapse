package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.Category

data class CreateCategoryRequest(
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val slug: String,
    val level: Int = 0,
    val path: String
) {
    fun toCategory(id: String? = null, imageUrl: String? = null) = Category(
        id = id ?: name,
        name = name,
        description = description,
        parentId = parentId,
        slug = slug,
        level = level,
        path = path,
        imageUrl = imageUrl
    )
}