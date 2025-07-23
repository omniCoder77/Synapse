package com.synapse.orderservice.infrastructure.outbound.postgres

import com.synapse.orderservice.domain.model.OrderItem
import com.synapse.orderservice.domain.port.driven.OrderItemRepository
import com.synapse.orderservice.infrastructure.outbound.postgres.entity.toEntity
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class OrderItemRepositoryImpl(private val r2dbcEntityTemplate: R2dbcEntityTemplate) : OrderItemRepository {
    override fun save(orderItems: List<OrderItem>): Mono<Void> {
        if (orderItems.isEmpty()) {
            return Mono.empty()
        }

        val sql =
            StringBuilder("INSERT INTO order_items (id, order_id, product_id, unit_price, quantity, created_at) VALUES ")
        val bindings = mutableListOf<Any>()
        var i = 1

        orderItems.forEach { orderItem ->
            sql.append("($${i++}, $${i++}, $${i++}, $${i++}, $${i++}, $${i++}),")
            bindings.add(orderItem.orderItemId)
            bindings.add(orderItem.orderId.value)
            bindings.add(orderItem.productId.value)
            bindings.add(orderItem.unitPrice)
            bindings.add(orderItem.quantity)
            bindings.add(orderItem.createdAt)
        }
        sql.setLength(sql.length - 1)

        return r2dbcEntityTemplate.databaseClient.sql(sql.toString()).bindValues(bindings).then()
    }

    override fun save(orderItem: OrderItem): Mono<UUID> {
        return r2dbcEntityTemplate.insert(orderItem.toEntity()).map { it.orderId }
    }
}