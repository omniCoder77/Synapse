package com.synapse.paymentservice.domain.model

import com.synapse.paymentservice.application.dto.request.OrderStatus

data class Order(
    val amount: Long = 0,
    var status: OrderStatus = OrderStatus.PAYMENT_PENDING,
    var razorpayOrderId: String = "",
    var receipt: String = ""
)