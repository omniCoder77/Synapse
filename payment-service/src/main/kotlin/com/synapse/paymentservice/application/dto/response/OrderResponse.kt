package com.synapse.paymentservice.application.dto.response

import com.synapse.paymentservice.application.dto.request.OrderStatus

data class OrderResponse(
    val razorPayOrderId: String,
    val orderStatus: OrderStatus
)