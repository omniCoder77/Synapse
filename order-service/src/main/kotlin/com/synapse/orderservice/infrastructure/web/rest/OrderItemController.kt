package com.synapse.orderservice.infrastructure.web.rest

import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.infrastructure.web.rest.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders/{orderId}/items")
class OrderItemController(private val orderRepository: OrderRepository) {

    @GetMapping
    fun getOrderItem(@PathVariable orderId: String, @RequestParam userId: String): ApiResponse {
        val order = orderRepository.getById(orderId) ?: return ApiResponse.Error(
            "Order not found.", HttpStatus.NOT_FOUND.value()
        )
        if (order.userId.value != userId) {
            return ApiResponse.Error(
                "You do not have permission to view this order.", HttpStatus.FORBIDDEN.value()
            )
        }
        return ApiResponse.Success(order.items)
    }

    @GetMapping("/{itemId}")
    fun getOrderItems(
        @PathVariable orderId: String, @RequestParam userId: String, @PathVariable itemId: String
    ): ApiResponse {
        val order = orderRepository.getById(orderId) ?: return ApiResponse.Error(
            "Order not found.", HttpStatus.NOT_FOUND.value()
        )
        if (order.userId.value != userId) {
            return ApiResponse.Error(
                "You do not have permission to view this order.", HttpStatus.FORBIDDEN.value()
            )
        }
        val orderItem = order.items.find { it.productId.value == itemId }
        return if (orderItem != null) {
            ApiResponse.Success(orderItem)
        } else {
            ApiResponse.Error("Order item not found.", HttpStatus.NOT_FOUND.value())
        }
    }
}