package com.synapse.orderservice.infrastructure.inbound.web.rest.dto

import com.synapse.orderservice.domain.model.Address
import com.synapse.orderservice.domain.model.PaymentMethod

data class CreateOrderRequest(
    val items: List<IdQuantity>,
    val shippingAddress: Address,
    val billingAddress: Address = shippingAddress,
    val paymentMethod: PaymentMethod,
    val currency: String = "USD",
    val notes: String? = null,
)

data class IdQuantity(
    val id: String, val amount: Int
)