package com.ethyllium.searchservice.infrastructure.kafka.event

data class BrandCreatedEvent(
    val id: String,
    val name: String,
    val website: String?,
    val description: String?,
    val logoUrl: String?,
    val slug: String
)