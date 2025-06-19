package com.ethyllium.authservice.infrastructure.adapters.communication

import com.ethyllium.authservice.domain.port.driven.OtpDeliveryService
import com.ethyllium.authservice.domain.port.driven.OtpGenerator
import com.twilio.rest.verify.v2.service.Verification
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

    override fun sendOtp(phoneNumber: String): Mono<Void> {
        return Mono.fromCallable {
            val verification = Verification.creator(pathServiceId, phoneNumber, "sms").create()
            logger.info("Sending verification to $phoneNumber", verification.accountSid)
        }.subscribeOn(Schedulers.boundedElastic()).then()
    }
}