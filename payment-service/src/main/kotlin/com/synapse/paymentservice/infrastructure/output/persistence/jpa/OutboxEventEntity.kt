package com.synapse.paymentservice.infrastructure.output.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "outbox")
data class OutboxEventEntity(
    @Id val id: String = UUID.randomUUID().toString(),
    val aggregateId: String = "",
    @CreationTimestamp() val createdAt: Instant = Instant.now(),
    @UpdateTimestamp var updatedAt: Instant = createdAt,
    @Version var version: Long = 0,
)