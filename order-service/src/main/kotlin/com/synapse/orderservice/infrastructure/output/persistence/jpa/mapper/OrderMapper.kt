package com.synapse.orderservice.infrastructure.output.persistence.jpa.mapper

import com.synapse.orderservice.domain.model.*
import com.synapse.orderservice.infrastructure.output.persistence.jpa.entity.OrderEntity
import com.synapse.orderservice.infrastructure.output.persistence.jpa.entity.OrderItemEntity
import com.synapse.orderservice.infrastructure.output.persistence.jpa.entity.OrderPricingEmbeddable

// Domain → Persistence
fun Order.toEntity(): OrderEntity = OrderEntity(
    orderId = id.value,
    userId = userId.value,
    status = status,
    items = items.mapTo(mutableListOf()) { it.toEntity() },
    pricing = pricing.toEmbeddable(),
    shippingAddress = shippingAddress.toEmbeddable(),
    billingAddress = billingAddress.toEmbeddable(),
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    trackingId = trackingId.value,
    createdAt = createdAt,
    updatedAt = updatedAt,
    metadata = metadata
)

private fun OrderItem.toEntity(): OrderItemEntity = OrderItemEntity(
    productId = productId.value,
    name = name,
    quantity = quantity,
    unitPrice = unitPrice.amount,
    currency = unitPrice.currency,
    discount = discount.amount,
    tax = tax.amount,
    imageUrl = imageUrl
)

private fun OrderPricing.toEmbeddable(): OrderPricingEmbeddable = OrderPricingEmbeddable(
    subtotal = subtotal.amount,
    tax = tax.amount,
    shippingCost = shippingCost.amount,
    discount = discount.amount,
    total = total.amount,
    currency = total.currency
)

// Persistence → Domain
fun OrderEntity.toDomain(): Order = Order(
    id = OrderId(orderId),
    userId = UserId(userId),
    status = status,
    items = items.map { it.toDomain() },
    pricing = pricing.toDomain(),
    shippingAddress = shippingAddress.toDomain(),
    billingAddress = billingAddress.toDomain(),
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    trackingId = TrackingId(trackingId),
    createdAt = createdAt,
    updatedAt = updatedAt,
    metadata = metadata
)

private fun OrderItemEntity.toDomain(): OrderItem = OrderItem(
    productId = ProductId(productId),
    name = name,
    quantity = quantity,
    unitPrice = Money(unitPrice, currency),
    discount = Money(discount, currency),
    tax = Money(tax, currency),
    imageUrl = imageUrl
)