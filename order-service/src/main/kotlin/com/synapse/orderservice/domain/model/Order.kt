package com.synapse.orderservice.domain.model

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class Order(
    val orderId: OrderId = OrderId(UUID.randomUUID()),
    val userId: UserId,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val subtotal: Double,
    val taxAmount: Double = 0.0,
    val shippingAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val currency: String = "USD",
    val billingAddress: Address,
    val shippingAddress: Address = billingAddress,
    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = createdAt,
    val confirmedAt: LocalDateTime? = null,
    val cancelledAt: LocalDateTime? = null,
    val paymentMethod: PaymentMethod,
    val paymentProvider: String? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val providerPaymentId: String? = null,
)

@JvmInline
value class OrderId(val value: UUID)

@JvmInline
value class ProductId(val value: UUID)

@JvmInline
value class UserId(val value: UUID)

data class OrderItem(
    val orderItemId: UUID = UUID.randomUUID(),
    val orderId: OrderId,
    val productId: ProductId,
    val unitPrice: Double,
    val quantity: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class Address(
    val firstName: String,
    val lastName: String,
    val company: String? = null,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String,
    val phone: String? = null
)


enum class PaymentStatus { PENDING, PAID, FAILED, REFUNDED }