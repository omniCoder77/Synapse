package com.synapse.orderservice.infrastructure.api.command

import com.synapse.orderservice.application.dto.request.OrderRequest
import com.synapse.orderservice.application.dto.response.OrderResponse
import com.synapse.orderservice.application.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(@RequestBody orderRequest: OrderRequest): ResponseEntity<OrderResponse> {
        val orderResponse = orderService.createOrder(orderRequest)
        return ResponseEntity(orderResponse, HttpStatus.CREATED)
    }
}