package com.ethyllium.authservice.infrastructure.adapters.outbound.communication

import com.ethyllium.authservice.domain.port.driven.OtpDeliveryService
import com.twilio.rest.verify.v2.service.Verification
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class SmsOtpAdapter(
    @Value("\${twilio.path-service-id}") private val pathServiceId: String
) : OtpDeliveryService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @CircuitBreaker(name = "twilio", fallbackMethod = "sendOtpFallback")
    override fun sendOtp(phoneNumber: String): Mono<Void> {
        return Mono.fromCallable {
            val verification = Verification.creator(pathServiceId, phoneNumber, "sms").create()
            logger.info("Sending verification to $phoneNumber", verification.accountSid)
        }.subscribeOn(Schedulers.boundedElastic()).then()
    }

    @Suppress("unused")
    private fun sendOtpFallback(phoneNumber: String, throwable: Throwable): Mono<Void> {
        logger.error("Twilio circuit breaker is open for phone number {}. Error: {}", phoneNumber, throwable.message)
        return Mono.error(RuntimeException("SMS service is temporarily unavailable. Please try again later."))
    }
}