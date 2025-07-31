package com.synapse.paymentservice.domain.model

data class OrderResponse(
    val orderId: String,
    val paymentStatus: PaymentStatus,
    val amount: Long,
    val notes: Map<String, String>?,
    val receipt: String?,
    val userId: String
)