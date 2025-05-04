package com.synapse.paymentservice.infrastructure.output.messaging.kafka

import com.synapse.paymentservice.domain.event.DomainEvent
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
        val event = when (event) {
            is DomainEvent.PaymentAuthorizedEvent -> {
                kafkaTemplate.executeInTransaction { it.send("payment-events", event) }
                OutboxEventEntity(aggregateId = event.paymentId)
            }

            is DomainEvent.PaymentFailedEvent -> {
                kafkaTemplate.executeInTransaction{ it.send("payment-events", event) }
                OutboxEventEntity(aggregateId = event.paymentId)
            }
        }
        jpaOutboxEventRepository.save(event)
    }
}