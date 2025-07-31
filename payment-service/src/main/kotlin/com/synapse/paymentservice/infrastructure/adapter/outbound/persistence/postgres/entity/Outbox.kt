package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("outbox")
data class Outbox(
    @Id val id: UUID = UUID.randomUUID(),
    val aggregateType: String,
    val aggregateId: UUID,
    val eventType: String,
    val payload: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
