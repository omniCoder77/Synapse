package com.synapse.orderservice.infrastructure.outbound.postgres.entity

import com.synapse.orderservice.domain.model.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

// Entity Classes
@Table("orders")
data class OrderEntity(
    @Id val orderId: UUID = UUID.randomUUID(),
    @Column("user_id") val userId: UUID,
    val orderStatus: String = OrderStatus.PENDING.name,
    val subtotal: Double,
    @Column("tax_amount") val taxAmount: Double = 0.0,
    @Column("shipping_amount") val shippingAmount: Double = 0.0,
    @Column("discount_amount") val discountAmount: Double = 0.0,
    val currency: String = "USD",
    @Column("billing_address") val billingAddress: Address,
    @Column("shipping_address") val shippingAddress: Address = billingAddress,
    val notes: String? = null,
    @Column("created_at") val createdAt: Instant = Instant.now(),
    @Column("updated_at") val updatedAt: Instant = createdAt,
    @Column("confirmed_at") val confirmedAt: LocalDateTime? = null,
    @Column("cancelled_at") val cancelledAt: LocalDateTime? = null,
    @Column("payment_method") val paymentMethod: String,
    @Column("payment_provider") val paymentProvider: String? = null,
    val paymentStatus: String = PaymentStatus.PENDING.name,
    @Column("provider_payment_id") val providerPaymentId: String? = null,
) {
    fun toDomain() = Order(
        orderId = OrderId(orderId),
        userId = UserId(userId),
        orderStatus = OrderStatus.valueOf(orderStatus),
        subtotal = subtotal,
        shippingAddress = shippingAddress,
        billingAddress = billingAddress,
        createdAt = createdAt,
        updatedAt = updatedAt,
        paymentStatus = PaymentStatus.valueOf(paymentStatus),
        paymentMethod = PaymentMethod.valueOf(paymentMethod),
        paymentProvider = paymentProvider,
    )
}

fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        orderId = this.orderId.value,
        userId = this.userId.value,
        paymentStatus = this.paymentStatus.name,
        subtotal = this.subtotal,
        shippingAddress = this.shippingAddress,
        billingAddress = this.billingAddress,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        paymentMethod = this.paymentMethod.name,
        paymentProvider = this.paymentProvider
    )
}