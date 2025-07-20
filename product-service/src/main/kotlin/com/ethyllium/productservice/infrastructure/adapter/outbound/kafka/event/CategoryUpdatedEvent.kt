package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class CategoryUpdatedEvent(
    val categoryId: String,
    val name: String?,
    val description: String?,
    val slug: String?,
    val parentId: String?,
    val imageUrl: String?
) : Event