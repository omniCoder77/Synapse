package com.synapse.orderservice.infrastructure.output.persistence.jpa.repository

import com.synapse.orderservice.infrastructure.output.persistence.jpa.entity.OrderEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JpaOrderEntityRepository : JpaRepository<OrderEntity, String> {
    fun findOrderEntityByTrackingId(trackingId: String): Optional<OrderEntity>

    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = 'CANCELLED' WHERE o.trackingId = :trackingId")
    fun cancelOrder(trackingId: String)

    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :name WHERE o.trackingId = :trackingId")
    fun updateOrderStatus(trackingId: String, name: String)
}