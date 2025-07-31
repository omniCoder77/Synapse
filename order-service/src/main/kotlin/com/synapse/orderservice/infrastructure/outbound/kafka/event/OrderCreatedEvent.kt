package com.synapse.orderservice.infrastructure.outbound.kafka.event

import com.synapse.orderservice.domain.model.PaymentMethod

data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val currency: String
)
