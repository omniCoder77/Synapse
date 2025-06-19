package com.synapse.orderservice.domain.port.driven

import com.synapse.orderservice.domain.model.Order

interface OrderRepository {
    fun save(order: Order): String
    fun delete(orderId: String)
    fun getById(orderId: String): Order?
}