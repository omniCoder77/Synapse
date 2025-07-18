package com.ethyllium.searchservice.infrastructure.kafka.event

data class SellerUpdatedEvent(
    val sellerId: String,
    val businessName: String?,
    val displayName: String?,
    val phone: String?
)