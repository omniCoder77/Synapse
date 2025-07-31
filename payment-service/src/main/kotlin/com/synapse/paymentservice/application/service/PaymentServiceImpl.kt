package com.synapse.paymentservice.application.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.paymentservice.domain.model.Refund
import com.synapse.paymentservice.domain.model.WebhookEvent
import com.synapse.paymentservice.domain.port.driven.CacheRepository
import com.synapse.paymentservice.domain.port.driven.OutboxRepository
import com.synapse.paymentservice.domain.port.driven.PaymentRepository
import com.synapse.paymentservice.domain.port.driven.RefundRepository
import com.synapse.paymentservice.domain.port.driver.PaymentService
import com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.utils.Topics
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.Outbox
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.temporal.ChronoUnit

@Component
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val outboxRepository: OutboxRepository,
    private val refundRepository: RefundRepository,
    private val cacheRepository: CacheRepository,
    private val resourceLoader: ResourceLoader,
    @Value("\${razorpay.key.id}") private val razorpayKeyId: String,
) : PaymentService {

    /**
     * Handles the webhook for payment verification. Events that our application is interested in are:
     * - `order.paid`
     * - `payment.failed`
     * - `refund.processed`
     * - `refund.failed`
     * - `refund.created`
     *
     * This method verifies the payload against the signature provided by Razorpay.
     * If the verification fails, it simply returns without further processing.
     */
    override fun webhook(message: String, signature: String, idempotencyKey: String): Mono<Boolean> {
        return cacheRepository.get(idempotencyKey, Boolean::class.java).flatMap { cachedValue ->
            Mono.just(cachedValue)
        }.switchIfEmpty(
            Mono.defer {
                if (!paymentRepository.verification(message, signature)) {
                    return@defer Mono.error(IllegalArgumentException("Invalid signature"))
                }

                val payload = jacksonObjectMapper().readValue(message, WebhookEvent::class.java)
                when (payload.event) {
                    "order.paid" -> {
                        paymentRepository.paid(payload.payload.order!!.entity.id).flatMap { updated ->
                            paymentRepository.findById(payload.payload.payment!!.entity.id).flatMap {
                                val outbox = Outbox(
                                    eventType = payload.event,
                                    payload = message,
                                    aggregateType = Topics.PAYMENT_SUCCESS,
                                    aggregateId = it.productOrderId
                                )

                                if (updated) {
                                    outboxRepository.save(outbox)
                                        .then(cacheRepository.set(idempotencyKey, true, 5, ChronoUnit.MINUTES))
                                        .thenReturn(true)
                                } else {
                                    cacheRepository.set(idempotencyKey, false, 5, ChronoUnit.MINUTES).thenReturn(false)
                                }
                            }
                        }
                    }

                    "payment.failed" -> {
                        paymentRepository.failed(payload.payload.payment!!.entity.orderId!!).flatMap { updated ->
                            paymentRepository.findById(payload.payload.payment.entity.id).flatMap {
                                val outbox = Outbox(
                                    eventType = payload.event,
                                    payload = message,
                                    aggregateType = Topics.PAYMENT_FAILURE,
                                    aggregateId = it.productOrderId
                                )

                                if (updated) {
                                    outboxRepository.save(outbox)
                                        .then(cacheRepository.set(idempotencyKey, true, 5, ChronoUnit.MINUTES))
                                        .thenReturn(true)
                                } else {
                                    cacheRepository.set(idempotencyKey, false, 5, ChronoUnit.MINUTES).thenReturn(false)
                                }
                            }
                        }
                    }

                    "refund.processed" -> {
                        refundRepository.refundProcessed(payload.payload.refund!!.entity.id)
                    }

                    "refund.failed" -> {
                        refundRepository.refundFailed(payload.payload.refund!!.entity.id)
                    }

                    "refund.created" -> {
                        val refund = Refund(
                            refundId = payload.payload.refund!!.entity.id,
                            amount = payload.payload.refund.entity.amount.toLong(),
                            paymentId = payload.payload.refund.entity.paymentId,
                            status = payload.payload.refund.entity.status,
                        )
                        refundRepository.save(refund).then(paymentRepository.refund(refund.refundId, payload.payload.refund.entity.paymentId))

                    }

                    else -> {
                        cacheRepository.set(idempotencyKey, true, 5, ChronoUnit.MINUTES).thenReturn(false)
                    }
                }
            })
    }

    override fun getOrderId(productOrderId: String, userId: String): Mono<String> {
        return paymentRepository.getByProductOrderId(productOrderId, userId).flatMap { paymentEntity ->
            val resource = resourceLoader.getResource("classpath:static/razorpay.html")
            Mono.fromCallable { resource.inputStream.readAllBytes().toString(StandardCharsets.UTF_8) }
                .map { htmlTemplate ->
                    htmlTemplate
                        .replace("{{RAZORPAY_KEY_ID}}", razorpayKeyId)
                        .replace("{{AMOUNT}}", paymentEntity.amountInSmallestDimension.toString())
                        .replace("{{ORDER_ID}}", paymentEntity.orderId)
                        .replace("{{USER_ID}}", userId)
                }
        }
    }
}