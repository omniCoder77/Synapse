package com.synapse.paymentservice.infrastructure.output.persistence.jpa

import com.synapse.paymentservice.application.dto.request.OrderStatus
import com.synapse.paymentservice.domain.model.Order
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id val orderId: String = UUID.randomUUID().toString(),
    val amount: Long = 0,
    @Enumerated(EnumType.STRING) var status: OrderStatus = OrderStatus.PAYMENT_PENDING,
    val createdAt: Instant = Instant.now(),
    var razorpayOrderId: String = "",
    var receipt: String = ""
) {
    fun toOrder() = Order(
        amount = this.amount, status = this.status, razorpayOrderId = this.razorpayOrderId, receipt = this.receipt
    )
}

fun Order.toOrderEntity() = OrderEntity(
    amount = amount, status = status, razorpayOrderId = razorpayOrderId, receipt = receipt
)