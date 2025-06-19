package com.ethyllium.searchservice.application.dto

import com.ethyllium.searchservice.domain.model.Product

data class CategoryDTO(
    val name: String,
    val parentName: String?,
    val path: String,
    val level: Int
) {
    companion object {
        fun fromDomain(category: Product.SearchCategory): CategoryDTO {
            return CategoryDTO(
                name = category.name,
                parentName = category.parentId,
                path = category.path,
                level = category.level
            )
        }
    }
}