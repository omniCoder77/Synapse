package com.synapse.paymentservice.domain.port.incoming

import com.synapse.paymentservice.application.dto.request.OrderRequest
import com.synapse.paymentservice.application.dto.response.OrderResponse

interface OrderServicePort {
  fun createOrder(orderRequest: OrderRequest): OrderResponse
  fun markPaid(orderId: String)
}