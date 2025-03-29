package com.ethyllium.searchservice.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class OutboxEvent(
    @Id val outboxEventId: String = UUID.randomUUID().toString(),
    val aggregateId: String = "",
    val eventType: String = "",
    val payload: String = "",
    var sent: Boolean = false
)