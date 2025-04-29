package com.synapse.orderservice.infrastructure.output.persistence.jpa

import com.synapse.orderservice.domain.model.Order
import com.synapse.orderservice.domain.model.OrderStatus
import com.synapse.orderservice.domain.model.PaymentMethod
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class OrderEntity(
    @Id val orderId: String = UUID.randomUUID().toString(),
    val trackingId: String? = null,
    val productId: String,
    val userId: String,
    val orderStatus: OrderStatus,
    @Embedded val shippingAddress: AddressEmbeddedEntity,
    @Embedded val billingAddress: AddressEmbeddedEntity = shippingAddress,
    val paymentMethod: PaymentMethod
) {
    fun toOrder() = Order(
        productId = productId,
        userId = userId,
        orderStatus = orderStatus,
        trackingId = this.trackingId,
        shippingAddress = shippingAddress.toAddress(),
        billingAddress = billingAddress.toAddress(),
        paymentMethod = paymentMethod
    )
}

fun Order.toOrderEntity() = OrderEntity(
    productId = this.productId,
    userId = this.userId,
    orderStatus = this.orderStatus,
    shippingAddress = shippingAddress.toAddressEmbeddedEntity(),
    billingAddress = billingAddress.toAddressEmbeddedEntity(),
    paymentMethod = this.paymentMethod,
    trackingId = this.trackingId
)
