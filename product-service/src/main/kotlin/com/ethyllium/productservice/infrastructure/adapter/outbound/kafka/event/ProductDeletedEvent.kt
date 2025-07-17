package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class ProductDeletedEvent(val productId: String): Event