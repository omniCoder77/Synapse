package com.synapse.paymentservice.domain.port.driver

import reactor.core.publisher.Mono

interface PaymentService {
    fun webhook(message: String, signature: String, idempotencyKey: String): Mono<Boolean>
    fun getOrderId(productOrderId: String, userId: String): Mono<String>
}