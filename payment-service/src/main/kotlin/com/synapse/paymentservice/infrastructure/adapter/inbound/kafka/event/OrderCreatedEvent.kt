package com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.event

data class OrderCreatedEvent(
    val orderId: String, val userId: String, val amount: Double, val paymentMethod: String, val currency: String
)
