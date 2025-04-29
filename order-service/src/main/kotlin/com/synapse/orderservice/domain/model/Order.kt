package com.synapse.orderservice.domain.model

data class Order(
    val trackingId: String? = null,
    val userId: String,
    val orderStatus: OrderStatus,
    val productId: String,
    val shippingAddress: Address,
    val billingAddress: Address = shippingAddress,
    val paymentMethod: PaymentMethod
)