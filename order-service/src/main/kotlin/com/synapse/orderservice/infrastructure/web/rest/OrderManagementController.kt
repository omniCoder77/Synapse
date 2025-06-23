package com.synapse.orderservice.infrastructure.web.rest

import com.synapse.orderservice.domain.model.*
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.infrastructure.output.kafka.dto.OrderCreated
import com.synapse.orderservice.infrastructure.web.grpc.ProductValidationService
import com.synapse.orderservice.infrastructure.web.rest.dto.ApiResponse
import com.synapse.orderservice.infrastructure.web.rest.dto.CreateOrderRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderManagementController(
    private val productValidationService: ProductValidationService,
    private val orderRepository: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, OrderCreated>
) {

    @PostMapping
    fun create(
        @RequestBody createOrderRequest: CreateOrderRequest, @RequestParam userId: String
    ): ResponseEntity<String> {
        val orderItems = productValidationService.createProduct(createOrderRequest.items)
        val order = Order(
            userId = UserId(userId),
            items = orderItems.first,
            status = OrderStatus.PENDING,
            pricing = OrderPricing(subtotal = Money(orderItems.second)),
            shippingAddress = createOrderRequest.shippingAddress.toDomain(),
            billingAddress = createOrderRequest.billingAddress.toDomain(),
            paymentMethod = createOrderRequest.paymentMethod,
        )
        val trackingId = orderRepository.save(order)
        kafkaTemplate.send("", OrderCreated(trackingId, order.userId.value))
        return ResponseEntity(trackingId, HttpStatus.CREATED)
    }

    @GetMapping("/{trackingId}")
    fun getOrderById(@PathVariable trackingId: String, @RequestParam userId: String): ApiResponse {
        val order = orderRepository.getByTrackingId(trackingId)

        return if (order != null) {
            if (userId == order.userId.value) {
                ApiResponse.Success(order)
            } else {
                ApiResponse.Error("You do not have permission to view this order.", HttpStatus.FORBIDDEN.value())
            }
        } else {
            ApiResponse.Error("Order not found.", HttpStatus.NOT_FOUND.value())
        }
    }

    @PostMapping("/{trackingId}/cancel")
    fun cancelOrder(
        @PathVariable trackingId: String, @RequestParam userId: String
    ): ResponseEntity<String> {
        val order = orderRepository.getByTrackingId(trackingId)
        return if (order != null) {
            if (userId == order.userId.value) {
                orderRepository.cancelOrder(trackingId)
                ResponseEntity("Order cancelled successfully.", HttpStatus.OK)
            } else {
                ResponseEntity("You do not have permission to cancel this order.", HttpStatus.FORBIDDEN)
            }
        } else {
            ResponseEntity("Order not found.", HttpStatus.NOT_FOUND)
        }
    }

    @PatchMapping("/{trackingId}/update")
    fun updateOrder(
        @PathVariable trackingId: String, @RequestParam userId: String, @RequestParam status: OrderStatus
    ): ResponseEntity<String> {
        val order = orderRepository.getById(trackingId)
        return if (order != null) {
            if (userId == order.userId.value) {
                orderRepository.updateOrderStatus(trackingId, status)
                ResponseEntity("Order updated successfully.", HttpStatus.OK)
            } else {
                ResponseEntity("You do not have permission to update this order.", HttpStatus.FORBIDDEN)
            }
        } else {
            ResponseEntity("Order not found.", HttpStatus.NOT_FOUND)
        }
    }
}
