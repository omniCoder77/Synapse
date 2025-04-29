package com.synapse.orderservice.infrastructure.output.httpClient.shipmentService

data class PlaceOrderRequest(
    val productId: String, val shippingAddress: PlaceOrderAddress
)