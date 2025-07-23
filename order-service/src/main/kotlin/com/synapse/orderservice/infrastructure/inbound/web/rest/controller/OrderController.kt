package com.synapse.orderservice.infrastructure.inbound.web.rest.controller

import com.synapse.orderservice.domain.port.driver.OrderService
import com.synapse.orderservice.infrastructure.inbound.web.rest.dto.CreateOrderRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(
        @RequestBody createOrderRequest: CreateOrderRequest, authentication: Authentication
    ): Mono<ResponseEntity<String>> {
        return orderService.createOrder(
            createOrderRequest.items,
            createOrderRequest.paymentMethod,
            createOrderRequest.billingAddress,
            createOrderRequest.shippingAddress,
            createOrderRequest.notes,
            createOrderRequest.currency,
            authentication.name
        ).then(Mono.just(ResponseEntity.ok("Order created")))
    }
}