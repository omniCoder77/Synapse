package com.synapse.paymentservice.infrastructure.output.messaging.kafka

import com.synapse.paymentservice.domain.event.DomainEvent
import com.synapse.paymentservice.domain.event.PaymentAuthorizedEvent
import com.synapse.paymentservice.domain.event.PaymentFailedEvent
import com.synapse.paymentservice.domain.port.outgoing.EventPublisher
import com.synapse.paymentservice.infrastructure.output.persistence.jpa.JpaOutboxEventEntityRepository
import com.synapse.paymentservice.infrastructure.output.persistence.jpa.OutboxEventEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, DomainEvent>,
    private val jpaOutboxEventRepository: JpaOutboxEventEntityRepository
) : EventPublisher {

    override fun publishEvent(event: DomainEvent) {
        when (event) {
            is PaymentAuthorizedEvent -> kafkaTemplate.send("payment-events", event)
            is PaymentFailedEvent -> kafkaTemplate.send("payment-events", event)
            else -> kafkaTemplate.send("unhandled-events", event)
        }
        val event = OutboxEventEntity(aggregateId = event.paymentId, createdAt = event.timestamp, payload = event.payload)
        jpaOutboxEventRepository.save(event)
    }
}