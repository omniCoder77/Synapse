package com.ethyllium.authservice.infrastructure.adapters.inbound.messaging

import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.ethyllium.authservice.domain.port.driven.EmailService
import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.communication.RedisEventPublisher
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.jpa.entity.LoginAttemptEntity
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers

@Component
class UserEventListener(
    private val eventTemplate: ReactiveRedisTemplate<String, UserRegisteredEvent>,
    private val emailService: EmailService,
    private val loginAttemptRepository: LoginAttemptRepository,
    @Value("\${mail.token.verification-ms}") private val emailVerificationTimeMilli: Int
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun listenToRegistrationEvents() {
        eventTemplate.listenToChannel(RedisEventPublisher.USER_REGISTERED_CHANNEL).map { message ->
            message.message
        }.publishOn(Schedulers.boundedElastic()).doOnNext { event ->
                logger.info("Event received for user {}. Processing side-effects.", event.email)
                try {
                    handleEmailSending(event)
                    handleLoginAttemptSave(event)
                } catch (e: Exception) {
                    logger.error("Failed to process side-effects for event: $event", e)
                }
            }.subscribe()

        logger.info(
            "Subscribed to user registration events on channel '{}'", RedisEventPublisher.USER_REGISTERED_CHANNEL
        )
    }

    private fun handleEmailSending(event: UserRegisteredEvent) {
        val expirationMinutes = emailVerificationTimeMilli / 1000 / 60
        emailService.sendVerificationEmail(event.email, event.userId.toString(), expirationMinutes).block()
        logger.info("Verification email sent to {}", event.email)
    }

    private fun handleLoginAttemptSave(event: UserRegisteredEvent) {
        loginAttemptRepository.save(
            LoginAttemptEntity(
                username = event.userId, deviceFingerprint = mutableListOf(event.deviceFingerprint)
            )
        ).block()
        logger.info("Login attempt saved for {}", event.userId)
    }
}