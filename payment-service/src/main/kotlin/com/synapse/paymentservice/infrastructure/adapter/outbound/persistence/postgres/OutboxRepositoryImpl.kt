package com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres

import com.synapse.paymentservice.domain.port.driven.OutboxRepository
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.Outbox
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OutboxRepositoryImpl(private val r2dbcEntityTemplate: R2dbcEntityTemplate) : OutboxRepository {
    override fun save(outbox: Outbox): Mono<Outbox> {
        return r2dbcEntityTemplate.insert(outbox)
    }
}