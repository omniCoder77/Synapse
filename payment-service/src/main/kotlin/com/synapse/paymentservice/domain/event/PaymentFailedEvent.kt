package com.synapse.paymentservice.domain.event

data class PaymentFailedEvent(val orderId: Any, override val paymentId: String, val status: String) :
    DomainEvent(paymentId = paymentId)