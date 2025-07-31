package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity

import com.synapse.paymentservice.domain.model.Payment
import com.synapse.paymentservice.domain.model.PaymentStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table(name = "payment")
data class PaymentEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("amount")
    val amountInSmallestDimension: Long,
    val orderId: String,
    val refundId: String? = null,
    val paymentId: String? = null,
    val productOrderId: UUID,
    var status: String = PaymentStatus.CREATED.name,
    val createdAt: Instant = Instant.now(),
    var receipt: String? = null,
    val userId: String,
) {
    fun toPayment() = Payment(
        id = this.id,
        amount = this.amountInSmallestDimension,
        status = PaymentStatus.valueOf(this.status),
        receipt = this.receipt,
        orderId = this.orderId,
        refundId = this.refundId,
        paymentId = this.paymentId,
        productOrderId = this.productOrderId,
        userId = this.userId,
    )
}

fun Payment.toPaymentEntity(productOrderId: UUID) = PaymentEntity(
    id = this.id,
    amountInSmallestDimension = amount,
    status = status.name,
    receipt = receipt,
    orderId = orderId,
    productOrderId = productOrderId,
    userId = this.userId,
    paymentId = this.paymentId,
    refundId = this.refundId,
    createdAt = Instant.now()
)