package com.ethyllium.searchservice.application.dto

import com.ethyllium.searchservice.domain.model.Product

data class CategoryDTO(
    val name: String,
    val path: String,
    val level: Int
) {
    companion object {
        fun fromDomain(category: Product.SearchCategory): CategoryDTO {
            return CategoryDTO(
                name = category.name,
                path = category.path,
                level = category.level
            )
        }
    }
}