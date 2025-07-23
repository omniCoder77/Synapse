package com.synapse.orderservice.domain.port.driven

import com.synapse.orderservice.domain.model.OrderItem
import reactor.core.publisher.Mono
import java.util.*

interface OrderItemRepository {
    fun save(orderItems: List<OrderItem>): Mono<Void>
    fun save(orderItem: OrderItem): Mono<UUID>
}