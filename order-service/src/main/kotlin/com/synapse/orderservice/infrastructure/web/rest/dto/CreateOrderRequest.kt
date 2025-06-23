package com.synapse.orderservice.infrastructure.web.rest.dto

import com.synapse.orderservice.domain.model.PaymentMethod

data class CreateOrderRequest(
    val items: List<IdAmount>,
    val shippingAddress: AddressDTO,
    val billingAddress: AddressDTO = shippingAddress,
    val paymentMethod: PaymentMethod
)

data class IdAmount(
    val id: String,
    val amount: Int)