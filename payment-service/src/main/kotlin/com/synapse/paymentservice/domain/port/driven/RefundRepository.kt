package com.synapse.paymentservice.domain.port.driven

import com.synapse.paymentservice.domain.model.Refund
import reactor.core.publisher.Mono

interface RefundRepository {
    fun refundProcessed(refundId: String): Mono<Boolean>
    fun refundFailed(refundId: String): Mono<Boolean>
    fun refundCreated(orderId: String)
    fun save(refund: Refund): Mono<Boolean>
}