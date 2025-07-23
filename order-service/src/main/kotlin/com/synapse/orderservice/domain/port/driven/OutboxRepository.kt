package com.synapse.orderservice.domain.port.driven

import reactor.core.publisher.Mono
import java.util.*

interface OutboxRepository {
    fun save(aggregateId: UUID, aggregateType: String, eventType: String, payload: String): Mono<UUID>
}