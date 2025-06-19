package com.synapse.orderservice.domain.port.driven

import com.synapse.orderservice.domain.model.Address

interface PlaceOrder {
    fun placeOrder(productId: String, shippingAddress: Address): String
}