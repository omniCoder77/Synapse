package com.synapse.orderservice.infrastructure.output.messaging.kafka

import com.synapse.orderservice.domain.model.Event
import com.synapse.orderservice.domain.port.driven.PublishEvent
import org.springframework.stereotype.Component

@Component
class KafkaPublisher: PublishEvent {
    override fun publishEvent(event: Event) {
        when(event) {
            is Event.OrderCreatedEvent -> {

            }
        }
    }
}