package com.ethyllium.authservice.infrastructure.adapters.outbound.communication

import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.ethyllium.authservice.domain.port.driver.UserEventPublisher
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

@Component
class RedisEventPublisher(
    private val eventTemplate: ReactiveRedisTemplate<String, UserRegisteredEvent>,
    @Value("\${event.retry.max-attempts:3}") private val maxRetryAttempts: Int
) : UserEventPublisher {

    companion object {
        const val USER_REGISTERED_CHANNEL = "users:registered"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val failedEvents = ConcurrentLinkedQueue<PendingEvent>()
    private val isRetrying = AtomicBoolean(false)

    data class PendingEvent(
        val channel: String,
        val event: UserRegisteredEvent,
        val timestamp: Instant = Instant.now(),
        val retryCount: Int = 0
    )

    @CircuitBreaker(name = "redis-publisher", fallbackMethod = "publishFallback")
    @Retryable(
        retryFor = [Exception::class], maxAttempts = 2, backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    override fun publish(event: UserRegisteredEvent): Mono<Long> {
        logger.info("Publishing event to channel '{}' for user {}", USER_REGISTERED_CHANNEL, event.email)

        return eventTemplate.convertAndSend(USER_REGISTERED_CHANNEL, event).doOnSuccess {
            logger.debug("Successfully published event for user {}", event.email)
            if (failedEvents.isNotEmpty()) {
                processPendingEvents()
            }
        }.doOnError { error ->
            logger.error("Failed to publish UserRegisteredEvent for ${event.email}", error)
        }
    }

    /**
     * Fallback method when circuit breaker is open or retries are exhausted
     */
    @Suppress("Unused")
    fun publishFallback(event: UserRegisteredEvent, ex: Exception): Mono<Long> {
        logger.warn("Redis unavailable, storing event locally for user: ${event.email}", ex)
        val pendingEvent = PendingEvent(USER_REGISTERED_CHANNEL, event)
        failedEvents.offer(pendingEvent)
        logger.info("Event stored locally. Pending events count: {}", failedEvents.size)
        return Mono.just(1L)
    }

    /**
     * Scheduled method to retry pending events
     */
    @Scheduled(fixedDelayString = "\${event.retry.delay-seconds:30}000")
    fun processPendingEvents() {
        if (failedEvents.isEmpty() || !isRetrying.compareAndSet(false, true)) {
            return
        }

        try {
            logger.info("Processing {} pending events", failedEvents.size)

            val eventsToRetry = mutableListOf<PendingEvent>()

            while (failedEvents.isNotEmpty()) {
                failedEvents.poll()?.let { eventsToRetry.add(it) }
            }

            eventsToRetry.forEach { pendingEvent ->
                retryPendingEvent(pendingEvent).subscribe({
                    logger.debug("Successfully retried event: {}", pendingEvent.event)
                }, { error ->
                    logger.warn("Failed to retry event, requeueing: {}", pendingEvent.event, error)
                    requeueEvent(pendingEvent)
                })
            }

        } finally {
            isRetrying.set(false)
        }
    }

    private fun retryPendingEvent(pendingEvent: PendingEvent): Mono<Long> {
        return eventTemplate.convertAndSend(pendingEvent.channel, pendingEvent.event).timeout(Duration.ofSeconds(5))
            .onErrorResume { error ->
                logger.debug("Retry failed for event: {}", pendingEvent.event, error)
                Mono.error(error)
            }
    }

    private fun requeueEvent(pendingEvent: PendingEvent) {
        val updatedEvent = pendingEvent.copy(retryCount = pendingEvent.retryCount + 1)
        if (updatedEvent.retryCount >= maxRetryAttempts || Duration.between(updatedEvent.timestamp, Instant.now())
                .toHours() > 24
        ) {
            logger.error(
                "Dropping event after {} retries or 24 hours: {}", updatedEvent.retryCount, updatedEvent.event
            )
            handleFailedEvent(updatedEvent)
            return
        }
        failedEvents.offer(updatedEvent)
    }

    /**
     * Handle events that cannot be delivered after all retries
     */
    private fun handleFailedEvent(event: PendingEvent) {
        // todo: Send alert to monitoring system
        logger.error("DEAD LETTER: Failed to deliver event after all retries: {}", event)
    }
}