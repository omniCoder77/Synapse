package com.synapse.paymentservice.domain.model

class Refund(
    val refundId: String,
    val paymentId: String,
    val amount: Long,
    val status: String,
)