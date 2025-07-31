package com.synapse.paymentservice.domain.port.driven

import com.synapse.paymentservice.domain.model.OrderResponse
import com.synapse.paymentservice.domain.model.Payment
import com.synapse.paymentservice.domain.model.PaymentRequest
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.PaymentEntity
import reactor.core.publisher.Mono
import java.util.*

interface PaymentRepository {
    fun createPaymentOrder(paymentRequest: PaymentRequest, userId: String): Mono<OrderResponse>
    fun save(payment: Payment, productOrderId: UUID): Mono<Payment>
    fun findById(id: String): Mono<Payment>
    fun findByRazorpayOrderId(razorpayOrderId: String): Mono<Payment>
    fun paid(razorpayOrderId: String): Mono<Boolean>
    fun verification(
        payload: String,
        signature: String
    ): Boolean

    fun getByProductOrderId(productOrderId: String, userId: String): Mono<PaymentEntity>
    fun failed(orderId: String): Mono<Boolean>
    fun refund(refundId: String, paymentId: String): Mono<Boolean>
}