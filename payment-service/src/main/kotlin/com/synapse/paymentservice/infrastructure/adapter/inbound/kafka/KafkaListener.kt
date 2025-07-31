package com.synapse.paymentservice.infrastructure.adapter.inbound.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.paymentservice.domain.model.Payment
import com.synapse.paymentservice.domain.model.PaymentRequest
import com.synapse.paymentservice.domain.model.PaymentStatus
import com.synapse.paymentservice.domain.port.driven.PaymentRepository
import com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.event.OrderCreatedEvent
import com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.utils.Topics
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaListener(private val paymentRepository: PaymentRepository) {

    @KafkaListener(topics = [Topics.ORDER_CREATED], groupId = "order_creation_request")
    fun orderCreated(message: String) {
        val orderCreatedEvent = jacksonObjectMapper().readValue(message, OrderCreatedEvent::class.java)
        val productOrderId = UUID.fromString(orderCreatedEvent.orderId)
        val paymentRequest =
            PaymentRequest(productOrderId, orderCreatedEvent.amount, PaymentStatus.CREATED)
        paymentRepository.createPaymentOrder(paymentRequest, orderCreatedEvent.userId).flatMap {
            val payment = Payment(
                amount = it.amount,
                status = it.paymentStatus,
                orderId = it.orderId,
                receipt = it.receipt,
                productOrderId = productOrderId,
                userId = it.userId
            )
            paymentRepository.save(payment, productOrderId)
        }.subscribe()
    }
}