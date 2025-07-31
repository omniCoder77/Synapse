package com.synapse.paymentservice.domain.port.driven

import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.Outbox
import reactor.core.publisher.Mono

interface OutboxRepository {
    fun save(outbox: Outbox): Mono<Outbox>
}