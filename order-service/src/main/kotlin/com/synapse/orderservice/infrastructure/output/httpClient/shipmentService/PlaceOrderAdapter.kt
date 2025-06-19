package com.synapse.orderservice.infrastructure.output.httpClient.shipmentService

import com.synapse.orderservice.domain.model.Address
import com.synapse.orderservice.domain.port.driven.PlaceOrder
import org.springframework.stereotype.Component

@Component
class PlaceOrderAdapter(private val placeOrderClient: PlaceOrderClient) : PlaceOrder {
    override fun placeOrder(
        productId: String, shippingAddress: Address
    ): String {
        val placeOrderRequest =
            PlaceOrderRequest(productId = productId, shippingAddress = shippingAddress.toPlaceOrderAddress())
        val order = placeOrderClient.placeOrder(placeOrderRequest)
        return order.trackingId
    }
}