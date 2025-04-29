package com.synapse.paymentservice.application.service

import com.synapse.paymentservice.application.dto.request.OrderRequest
import com.synapse.paymentservice.application.dto.response.OrderResponse
import com.synapse.paymentservice.domain.exception.OrderNotFoundException
import com.synapse.paymentservice.domain.port.outgoing.OrderRepositoryPort
import com.synapse.paymentservice.infrastructure.output.razorpay.RazorpayPaymentGateway
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepositoryPort: OrderRepositoryPort,
    private val razorpayPaymentGateway: RazorpayPaymentGateway
) {
    fun paid(orderId: String) {
        val order = orderRepositoryPort.findById(orderId) ?: throw OrderNotFoundException(orderId)
        orderRepositoryPort.paid(order)
    }

    fun createOrder(productOrderRequest: OrderRequest): OrderResponse {
        return razorpayPaymentGateway.createOrder(productOrderRequest)
    }
}