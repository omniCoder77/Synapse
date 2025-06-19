package com.synapse.paymentservice.domain.port.outgoing

import com.synapse.paymentservice.domain.event.DomainEvent

interface EventPublisher {
    fun publishEvent(event: DomainEvent)
}