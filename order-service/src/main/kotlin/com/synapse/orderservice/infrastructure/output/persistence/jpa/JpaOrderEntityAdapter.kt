package com.synapse.orderservice.infrastructure.output.persistence.jpa

import com.synapse.orderservice.domain.model.Order
import com.synapse.orderservice.domain.port.driven.OrderRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class JpaOrderEntityAdapter(private val jpaOrderEntityRepository: JpaOrderEntityRepository) : OrderRepository {
    override fun save(order: Order): String {
        return jpaOrderEntityRepository.save(order.toOrderEntity()).orderId
    }

    override fun delete(orderId: String) {
        jpaOrderEntityRepository.deleteById(orderId)
    }

    override fun getById(orderId: String): Order? {
        return jpaOrderEntityRepository.findById(orderId).getOrNull()?.toOrder()
    }
}