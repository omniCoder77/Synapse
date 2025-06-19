package com.synapse.orderservice.application.dto.response

data class OrderResponse(
    val orderId: String,
    val trackingId: String?
)