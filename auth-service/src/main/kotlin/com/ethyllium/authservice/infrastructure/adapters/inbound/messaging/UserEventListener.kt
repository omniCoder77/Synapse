package com.ethyllium.authservice.infrastructure.adapters.inbound.messaging

import com.ethyllium.authservice.domain.model.UserRegisteredEvent
import com.ethyllium.authservice.domain.port.driven.EmailService
import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.infrastructure.adapters.outbound.communication.RedisEventPublisher
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.LoginAttemptEntity
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.Duration

@Component
class UserEventListener(
    private val eventTemplate: ReactiveRedisTemplate<String, UserRegisteredEvent>,
    private val emailService: EmailService,
    private val loginAttemptRepository: LoginAttemptRepository,
    @Value("\${mail.token.verification-ms}") private val emailVerificationTimeMilli: Int,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val cpuScheduler: Scheduler
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun listenToRegistrationEvents() {
        handleLoginAttemptSave()
        eventTemplate.listenToChannel(RedisEventPublisher.USER_REGISTERED_CHANNEL).map { message ->
            message.message
        }.subscribeOn(Schedulers.parallel()).doOnNext { event ->
            try {
                handleEmailSending(event)
                handlePasswordUpdate(event)
            } catch (e: Exception) {
                logger.error("Failed to process side-effects for event: $event", e)
            }
        }.subscribe()
    }

    private fun handlePasswordUpdate(event: UserRegisteredEvent) {
        val password = passwordEncoder.encode(event.password)
        userRepository.updatePassword(event.userId, password).subscribeOn(cpuScheduler).subscribe {}
    }

    private fun handleEmailSending(event: UserRegisteredEvent) {
        val expirationMinutes = emailVerificationTimeMilli / 1000 / 60
        emailService.sendVerificationEmail(event.email, event.userId.toString(), expirationMinutes).subscribe()
    }

    private fun handleLoginAttemptSave() {
        eventTemplate.listenToChannel(RedisEventPublisher.USER_REGISTERED_CHANNEL).map { message -> message.message }
            .subscribeOn(Schedulers.parallel())
            .map { LoginAttemptEntity(it.userId, deviceFingerprint = mutableListOf(it.deviceFingerprint)) }
            .bufferTimeout(1000, Duration.ofSeconds(1)).filter { it.isNotEmpty() }.subscribe({
                loginAttemptRepository.save(it).subscribe()
            }, { error ->
                logger.error("Failed to save login attempts", error)
            })
    }
}