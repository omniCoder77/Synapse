package com.synapse.paymentservice.infrastructure.output.persistence.jpa

import com.synapse.paymentservice.domain.model.Order
import com.synapse.paymentservice.domain.port.outgoing.OrderRepositoryPort
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class OrderRepositoryAdapter(private val jpaOrderEntityRepository: JpaOrderEntityRepository) : OrderRepositoryPort {
    override fun save(order: Order): Order {
        return jpaOrderEntityRepository.save(order.toOrderEntity()).toOrder()
    }

    override fun findById(id: String): Order? {
        return jpaOrderEntityRepository.findById(id).getOrNull()?.toOrder()
    }

    override fun paid(order: Order) {
        jpaOrderEntityRepository.paid(order.razorpayOrderId)
    }
}