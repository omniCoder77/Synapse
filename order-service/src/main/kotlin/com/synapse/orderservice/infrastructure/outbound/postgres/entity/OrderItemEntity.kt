package com.synapse.orderservice.infrastructure.outbound.postgres.entity

import com.synapse.orderservice.domain.model.OrderId
import com.synapse.orderservice.domain.model.OrderItem
import com.synapse.orderservice.domain.model.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("order_items")
data class OrderItemEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("order_id") val orderId: UUID,
    @Column("product_id") val productId: UUID,
    @Column("unit_price") val unitPrice: Double,
    val quantity: Int,
    @Column("created_at") val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): OrderItem {
        return OrderItem(
            orderItemId = id,
            productId = ProductId(productId),
            quantity = quantity,
            unitPrice = unitPrice,
            orderId = OrderId(orderId)
        )
    }
}
fun OrderItem.toEntity(): OrderItemEntity {
    return OrderItemEntity(
        orderId = orderItemId, productId = this.productId.value, quantity = this.quantity, unitPrice = this.unitPrice
    )
}