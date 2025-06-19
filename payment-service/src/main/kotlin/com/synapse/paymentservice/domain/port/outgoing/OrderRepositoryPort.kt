package com.synapse.paymentservice.domain.port.outgoing

import com.synapse.paymentservice.domain.model.Order

interface OrderRepositoryPort {
    fun save(order: Order): Order
    fun findById(id: String): Order?
    fun paid(order: Order)
}