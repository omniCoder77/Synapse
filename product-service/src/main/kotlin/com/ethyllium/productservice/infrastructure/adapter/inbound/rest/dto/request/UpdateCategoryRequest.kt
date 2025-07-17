package com.ethyllium.productservice.infrastructure.adapter.inbound.rest.dto.request

data class UpdateCategoryRequest(
    val name: String? = null,
    val description: String? = null,
    val slug: String? = null,
    val parentId: String? = null
)