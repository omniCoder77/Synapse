package com.ethyllium.authservice.domain.port.driver

import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import reactor.core.publisher.Mono

interface UserEventPublisher {
    fun publish(event: UserRegisteredEvent): Mono<Long>
}
