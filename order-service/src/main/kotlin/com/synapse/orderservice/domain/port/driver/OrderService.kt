package com.synapse.orderservice.domain.port.driver

import com.synapse.orderservice.domain.model.*
import com.synapse.orderservice.infrastructure.inbound.rest.rest.dto.IdQuantity
import reactor.core.publisher.Mono
import java.util.*

interface OrderService {
    fun getOrderById(orderId: String): Mono<Order>
    fun updateOrder(
        userId: String,
        orderId: OrderId,
        status: OrderStatus?,
        items: List<OrderItem>,
        paymentMethod: PaymentMethod?,
        paymentStatus: PaymentStatus?
    )

    fun createOrder(
        items: List<IdQuantity>,
        paymentMethod: PaymentMethod,
        billingAddress: Address,
        shippingAddress: Address,
        notes: String?,
        currency: String,
        userId: String
    ): Mono<UUID>
}