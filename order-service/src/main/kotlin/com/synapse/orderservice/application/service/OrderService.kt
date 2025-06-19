package com.synapse.orderservice.application.service

import com.synapse.orderservice.application.dto.request.OrderRequest
import com.synapse.orderservice.application.dto.response.OrderResponse
import com.synapse.orderservice.domain.exception.InvalidProductException
import com.synapse.orderservice.domain.model.Event
import com.synapse.orderservice.domain.model.OrderStatus
import com.synapse.orderservice.domain.model.PaymentMethod
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.domain.port.driven.PlaceOrder
import com.synapse.orderservice.domain.port.driven.ProductAmount
import com.synapse.orderservice.domain.port.driven.PublishEvent
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val productAmount: ProductAmount,
    private val publishEvent: PublishEvent,
    private val orderRepository: OrderRepository,
    private val placeOrder: PlaceOrder
) {
    fun createOrder(orderRequest: OrderRequest): OrderResponse {
        val amount = productAmount.calculateAmount(orderRequest.productId)
        if (amount == null) throw InvalidProductException(orderRequest.productId)
        val response = when (orderRequest.paymentMethod) {
            PaymentMethod.CASH_ON_DELIVERY -> {
                val trackingId = placeOrder.placeOrder(
                    productId = orderRequest.productId, shippingAddress = orderRequest.shippingAddress
                )
                val order = orderRequest.toOrder(
                    trackingId = trackingId,
                    orderStatus = OrderStatus.PLACED,
                    paymentMethod = PaymentMethod.CASH_ON_DELIVERY
                )
                val orderId = orderRepository.save(order)
                OrderResponse(orderId = orderId, trackingId = trackingId)
            }

            PaymentMethod.ONLINE -> {
                publishEvent.publishEvent(Event.OrderCreatedEvent(productId = orderRequest.productId, amount = amount))
                val order =
                    orderRequest.toOrder(orderStatus = OrderStatus.PENDING, paymentMethod = PaymentMethod.ONLINE)
                val orderId = orderRepository.save(order)
                OrderResponse(orderId = orderId, trackingId = null)
            }
        }
        return response
    }
}