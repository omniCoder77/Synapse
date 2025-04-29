package com.synapse.paymentservice.infrastructure.output.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "outbox")
data class OutboxEventEntity(
    @Id val id: String = UUID.randomUUID().toString(),
    val aggregateId: String = "",
    @Lob val payload: String = "",
    val createdAt: Instant = Instant.now()
)