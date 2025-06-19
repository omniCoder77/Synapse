package com.synapse.orderservice.domain.model

sealed interface Event {
    data class OrderCreatedEvent(val productId: String, val amount: Double): Event
}