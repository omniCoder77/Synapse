package com.ethyllium.searchservice.infrastructure.kafka.event

data class CategoryUpdatedEvent(
    val categoryId: String,
    val name: String?,
    val description: String?,
    val slug: String?,
    val parentId: String?
)