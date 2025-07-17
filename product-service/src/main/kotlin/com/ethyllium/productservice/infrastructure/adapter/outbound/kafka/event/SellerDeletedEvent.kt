package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class SellerDeletedEvent(val sellerId: String) : Event