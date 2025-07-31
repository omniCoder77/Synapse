package com.ethyllium.searchservice.infrastructure.adapter.outbound.kafka.event

data class BrandUpdatedEvent(
    val brandId: String,
    val fileUrl: String?,
    val description: String?,
    val logoUrl: String?,
    val website: String?,
    val slug: String?,
    val name: String?
)