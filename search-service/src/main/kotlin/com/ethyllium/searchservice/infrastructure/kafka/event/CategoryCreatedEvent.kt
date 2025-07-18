package com.ethyllium.searchservice.infrastructure.kafka.event

data class CategoryCreatedEvent(
    val id: String,
    val name: String,
    val description: String?,
    val parentId: String?,
    val slug: String,
    val level: Int,
    val path: String,
    val imageUrl: String?
)