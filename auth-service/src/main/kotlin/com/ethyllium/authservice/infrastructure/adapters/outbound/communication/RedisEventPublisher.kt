package com.ethyllium.authservice.infrastructure.adapters.outbound.communication

import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.ethyllium.authservice.domain.port.driver.UserEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RedisEventPublisher(
    private val eventTemplate: ReactiveRedisTemplate<String, UserRegisteredEvent>
) : UserEventPublisher {

    companion object {
        const val USER_REGISTERED_CHANNEL = "users:registered"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun publish(event: UserRegisteredEvent): Mono<Long> {
        logger.info("Publishing event to channel '{}' for user {}", USER_REGISTERED_CHANNEL, event.email)
        return eventTemplate.convertAndSend(USER_REGISTERED_CHANNEL, event)
            .doOnError { logger.error("Failed to publish UserRegisteredEvent for ${event.email}", it) }
    }
}