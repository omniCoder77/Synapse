package com.ethyllium.productservice.infrastructure.adapter.outbound.kafka.event

import com.ethyllium.productservice.domain.model.Product

data class ProductUpdatedEvent(val product: Product): Event