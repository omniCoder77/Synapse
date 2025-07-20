package com.ethyllium.productservice.infrastructure.adapter.outbound.persistence.postgres.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.temporal.ChronoUnit

@Document("outbox_event")
data class OutboxEventEntity(
    @Id var id: String? = null,
    val eventTopic: String, // This is topic for kafka
    val payload: String,
    val metadata: String = "",
    val createdAt: Instant = Instant.now(),
    var publishedAt: Instant? = null,
    var status: EventStatus = EventStatus.PENDING,
    @Indexed(expireAfter = "604800s") var expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS)
)

enum class EventStatus {
    PENDING, PUBLISHED, FAILED
}