package com.synapse.orderservice.infrastructure.output.httpClient.shipmentService

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient
interface PlaceOrderClient {
    @PostMapping("/shipment/ship")
    fun placeOrder(placeOrderRequest: PlaceOrderRequest): PlaceOrderResponse
}