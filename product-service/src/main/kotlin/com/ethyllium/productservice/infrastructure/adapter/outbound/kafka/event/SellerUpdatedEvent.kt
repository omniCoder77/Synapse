package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class SellerUpdatedEvent(
    val sellerId: String,
    val businessName: String?,
    val displayName: String?,
    val phone: String?
) : Event