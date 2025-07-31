package com.ethyllium.productservice.infrastructure.adapter.inbound.kafka.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.util.*

// Only handles insert events (op="c")
data class OutboxEvent(
    val after: OutboxRecord,
    val source: Source,
    @JsonProperty("ts_ms") val timestampMs: Long
)

data class OutboxRecord(
    val id: UUID,
    @JsonProperty("aggregate_type") val aggregateType: String,
    @JsonProperty("aggregate_id") val aggregateId: String,
    @JsonProperty("event_type") val eventType: String,
    val payload: String,  // Serialized UserRegisteredEvent
    @JsonProperty("created_at") val createdAt: Instant,
    val status: String
)

data class Source(
    val version: String,
    val connector: String,
    val name: String,
    @JsonProperty("ts_ms") val timestampMs: Long,
    val snapshot: String,
    val db: String,
    val schema: String,
    val table: String,
    @JsonProperty("txId") val transactionId: Long,
    val lsn: Long
)

data class SellerRegisteredEvent(
    val userId: String,
    val email: String,
    val role: List<String>,
    val phoneNumber: String,
    val name: String
)