package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

import com.ethyllium.productservice.domain.model.Category
import java.util.*

data class CreateCategoryRequest(
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val slug: String,
    val level: Int = 0,
    val path: String
) {
    fun toCategory(imageUrl: String? = null) = Category(
        name = name,
        description = description,
        parentId = parentId,
        slug = slug,
        level = level,
        path = path,
        imageUrl = imageUrl,
        id = UUID.randomUUID().toString()
    )
}