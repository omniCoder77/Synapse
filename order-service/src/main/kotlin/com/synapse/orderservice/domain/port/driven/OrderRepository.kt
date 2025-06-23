package com.synapse.orderservice.domain.port.driven

import com.synapse.orderservice.domain.model.Order
import com.synapse.orderservice.domain.model.OrderStatus

interface OrderRepository {
    fun save(order: Order): String
    fun delete(orderId: String)
    fun getById(orderId: String): Order?
    fun getByTrackingId(trackingId: String): Order?
    fun cancelOrder(trackingId: String)
    fun updateOrderStatus(trackingId: String, status: OrderStatus)
}