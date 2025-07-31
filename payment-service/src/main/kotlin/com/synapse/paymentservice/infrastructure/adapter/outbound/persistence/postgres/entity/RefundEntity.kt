package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity

import com.synapse.paymentservice.domain.model.Refund
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("refund")
data class RefundEntity(
    @Id val refundId: String,
    val paymentId: String,
    val amount: Long,
    val status: String,
    @CreatedDate val createdAt: Instant = Instant.now(),
    @LastModifiedDate val updatedAt: Instant = createdAt,
)

fun Refund.toEntity() = RefundEntity(
    refundId = this.refundId,
    paymentId = this.paymentId,
    amount = this.amount,
    status = this.status,
)