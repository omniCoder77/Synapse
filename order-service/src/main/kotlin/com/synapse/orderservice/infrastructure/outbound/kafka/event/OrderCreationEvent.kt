package com.synapse.orderservice.infrastructure.outbound.kafka.event

import com.synapse.orderservice.domain.model.PaymentMethod
import com.synapse.orderservice.infrastructure.inbound.rest.rest.dto.IdQuantity

data class OrderCreationEvent(
    val orderItems: List<IdQuantity>,
    val userId: String,
    val orderId: String,
    val currency: String,
    val paymentMethod: PaymentMethod
)