package com.synapse.paymentservice.domain.event

data class PaymentAuthorizedEvent(val orderId: String, override val paymentId: String, val status: String) :
    DomainEvent(paymentId = paymentId) {
}