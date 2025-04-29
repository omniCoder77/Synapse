package com.synapse.paymentservice.domain.event

data class PaymentEvent(val orderId: String, val paymentId: String, val status: String)