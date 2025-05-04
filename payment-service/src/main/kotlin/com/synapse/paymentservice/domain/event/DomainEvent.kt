package com.synapse.paymentservice.domain.event

sealed interface DomainEvent {
    data class PaymentAuthorizedEvent(val razorpayOrderId: String, val paymentId: String, val status: String) : DomainEvent
    data class PaymentFailedEvent(val razorpayOrderId: String, val paymentId: String, val status: String) : DomainEvent
}