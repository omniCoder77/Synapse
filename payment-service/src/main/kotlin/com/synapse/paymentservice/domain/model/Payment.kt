package com.synapse.paymentservice.domain.model

import java.util.*

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val amount: Long,
    var status: PaymentStatus = PaymentStatus.CREATED,
    var orderId: String,
    var receipt: String?,
    val paymentId: String? = null,
    val refundId: String? = null,
    val productOrderId: UUID,
    val userId: String
)