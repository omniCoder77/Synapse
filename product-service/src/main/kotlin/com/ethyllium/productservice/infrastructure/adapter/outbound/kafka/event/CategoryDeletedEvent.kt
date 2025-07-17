package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

data class CategoryDeletedEvent(val categoryId: String) : Event