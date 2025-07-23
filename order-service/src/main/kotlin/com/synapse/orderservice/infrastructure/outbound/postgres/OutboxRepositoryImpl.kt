package com.synapse.orderservice.infrastructure.outbound.postgres

import com.synapse.orderservice.domain.port.driven.OutboxRepository
import com.synapse.orderservice.infrastructure.outbound.postgres.entity.Outbox
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class OutboxRepositoryImpl(private val r2dbcEntityTemplate: R2dbcEntityTemplate) : OutboxRepository {
    override fun save(
        aggregateId: UUID,
        aggregateType: String,
        eventType: String,
        payload: String
    ): Mono<UUID> {
        val outbox = Outbox(UUID.randomUUID(), aggregateType, aggregateId, eventType, payload)
        return r2dbcEntityTemplate.insert(outbox).map { it.id }
    }
}