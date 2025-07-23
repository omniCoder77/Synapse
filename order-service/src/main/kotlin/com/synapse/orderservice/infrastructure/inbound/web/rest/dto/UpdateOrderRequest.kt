package com.synapse.orderservice.infrastructure.inbound.web.rest.dto

import com.synapse.orderservice.domain.model.*

data class UpdateOrderRequest(
    val orderId: OrderId,
    val status: OrderStatus?,
    val items: List<OrderItem> = emptyList(),
    val paymentMethod: PaymentMethod?,
    val paymentStatus: PaymentStatus?,
)