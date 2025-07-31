package com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("outbox")
data class Outbox(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("aggregate_type") val aggregateType: String,
    @Column("aggregate_id") val aggregateId: UUID,
    @Column("event_type") val eventType: String,
    @Column("payload") val payload: String,
    @Column("headers") val headers: String? = null,
    @Column("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("processed_at") var processedAt: LocalDateTime? = null,
    @Column("status") var status: String = "PENDING"
)