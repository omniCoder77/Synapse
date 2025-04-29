package com.synapse.orderservice.application.dto.request

import com.synapse.orderservice.domain.model.Address
import com.synapse.orderservice.domain.model.Order
import com.synapse.orderservice.domain.model.OrderStatus
import com.synapse.orderservice.domain.model.PaymentMethod

data class OrderRequest(
    val productId: String,
    val userId: String,
    val shippingAddress: Address,
    val billingAddress: Address = shippingAddress,
    val paymentMethod: PaymentMethod
) {
    fun toOrder(trackingId: String? = null, orderStatus: OrderStatus, paymentMethod: PaymentMethod) = Order(
        trackingId = trackingId,
        productId = productId,
        userId = userId,
        shippingAddress = shippingAddress,
        billingAddress = billingAddress,
        orderStatus = orderStatus,
        paymentMethod = paymentMethod,
    )
}