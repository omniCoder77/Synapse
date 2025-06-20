package com.ethyllium.authservice.domain.port.driven

import reactor.core.publisher.Mono

interface OtpDeliveryService {
    fun sendOtp(phoneNumber: String): Mono<Void>
}