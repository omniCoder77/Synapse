package com.synapse.orderservice.infrastructure.output.persistence.jpa.adapter

import com.synapse.orderservice.domain.exception.OrderNotFoundException
import com.synapse.orderservice.domain.model.Order
import com.synapse.orderservice.domain.model.OrderStatus
import com.synapse.orderservice.domain.port.driven.OrderRepository
import com.synapse.orderservice.infrastructure.output.persistence.jpa.mapper.toDomain
import com.synapse.orderservice.infrastructure.output.persistence.jpa.mapper.toEntity
import com.synapse.orderservice.infrastructure.output.persistence.jpa.repository.JpaOrderEntityRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class JpaOrderEntityAdapter(private val jpaOrderEntityRepository: JpaOrderEntityRepository) : OrderRepository {
    override fun save(order: Order): String {
        return jpaOrderEntityRepository.save(order.toEntity()).trackingId
    }

    override fun delete(orderId: String) {
        jpaOrderEntityRepository.deleteById(orderId)
    }

    override fun getById(orderId: String): Order? {
        return jpaOrderEntityRepository.findById(orderId).getOrNull()?.toDomain()
    }

    override fun getByTrackingId(trackingId: String): Order? {
        return jpaOrderEntityRepository.findOrderEntityByTrackingId(trackingId).getOrNull()?.toDomain()
    }

    override fun cancelOrder(trackingId: String) {
        val order = jpaOrderEntityRepository.findOrderEntityByTrackingId(trackingId).getOrNull() ?: throw OrderNotFoundException(
            "Order with tracking ID $trackingId not found"
        )
        jpaOrderEntityRepository.cancelOrder(order.trackingId)
    }

    override fun updateOrderStatus(
        trackingId: String,
        status: OrderStatus
    ) {
        jpaOrderEntityRepository.updateOrderStatus(trackingId, status.name)
    }
}