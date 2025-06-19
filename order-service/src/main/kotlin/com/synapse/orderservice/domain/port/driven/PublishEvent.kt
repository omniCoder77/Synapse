package com.synapse.orderservice.domain.port.driven

import com.synapse.orderservice.domain.model.Event

interface PublishEvent {
    fun publishEvent(event: Event)
}