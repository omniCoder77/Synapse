package com.synapse.paymentservice.application.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.synapse.paymentservice.domain.port.driven.CacheRepository
import com.synapse.paymentservice.domain.port.driven.OutboxRepository
import com.synapse.paymentservice.domain.port.driven.PaymentRepository
import com.synapse.paymentservice.domain.port.driven.RefundRepository
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.Outbox
import com.synapse.paymentservice.util.TestUtil
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.temporal.ChronoUnit

class PaymentServiceImplTest {

    private lateinit var paymentRepository: PaymentRepository
    private lateinit var outboxRepository: OutboxRepository
    private lateinit var refundRepository: RefundRepository
    private lateinit var cacheRepository: CacheRepository
    private lateinit var paymentService: PaymentServiceImpl

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        paymentRepository = mockk()
        outboxRepository = mockk()
        refundRepository = mockk()
        cacheRepository = mockk()
        paymentService = PaymentServiceImpl(
            paymentRepository,
            outboxRepository,
            refundRepository,
            cacheRepository,
            resourceLoader
        )

        every { outboxRepository.save(any<Outbox>()) } returns Mono.just(TestUtil.createOutbox())
        every { cacheRepository.set(any(), true, 5, ChronoUnit.MINUTES) } returns Mono.empty()
        every { cacheRepository.get(any(), Boolean::class.java) } returns Mono.empty()
        every { paymentRepository.findById(any()) } returns Mono.just(TestUtil.createPayment())
        every { paymentRepository.verification(any(), any()) } returns true
    }

    @Test
    fun `webhook should process order_paid event successfully`() {
        val webhookPayload = TestUtil.orderPaidPayload
        val signature = TestUtil.orderPaidSignature
        val idempotencyKey = TestUtil.orderPaidEventId

        every { paymentRepository.paid(any()) } returns Mono.just(true)

        StepVerifier.create(paymentService.webhook(webhookPayload, signature, idempotencyKey))
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `webhook should process payment_failed event successfully`() {
        val webhookPayload = TestUtil.paymentFailedPayload
        val signature = TestUtil.paymentFailedSignature
        val idempotencyKey = TestUtil.paymentFailedEventId

        every { paymentRepository.failed(any()) } returns Mono.just(true)

        StepVerifier.create(paymentService.webhook(webhookPayload, signature, idempotencyKey))
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `webhook should process refund_created event successfully`() {
        val webhookPayload = TestUtil.refundCreatedPayload
        val signature = TestUtil.refundCreatedSignature
        val idempotencyKey = TestUtil.refundCreatedEventId

        every { cacheRepository.get(idempotencyKey, Boolean::class.java) } returns Mono.empty()
        every { paymentRepository.verification(webhookPayload, signature) } returns true
        every { refundRepository.save(any()) } returns Mono.just(true)
        every { paymentRepository.refund(any(), any()) } returns Mono.just(true)

        StepVerifier.create(paymentService.webhook(webhookPayload, signature, idempotencyKey))
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `webhook should process refund_processed event successfully`() {
        val webhookPayload = TestUtil.refundProcessedPayload
        val signature = TestUtil.refundProcessedSignature
        val idempotencyKey = TestUtil.refundProcessedEventId

        every { refundRepository.refundProcessed(any()) } returns Mono.just(true)

        StepVerifier.create(paymentService.webhook(webhookPayload, signature, idempotencyKey))
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `webhook should handle unhandled events gracefully`() {
        val webhookPayload = TestUtil.createWebhookPayload("payment.authorized")
        val webhookJson = objectMapper.writeValueAsString(webhookPayload)
        val signature = "valid_signature"
        val idempotencyKey = "idempotency_key_unhandled"

        StepVerifier.create(paymentService.webhook(webhookJson, signature, idempotencyKey))
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `webhook should fail on invalid signature`() {
        val webhookPayload = TestUtil.createWebhookPayload("order.paid")
        val webhookJson = objectMapper.writeValueAsString(webhookPayload)
        val signature = "invalid_signature"
        val idempotencyKey = "idempotency_key_invalid_sig"

        every { paymentRepository.verification(webhookJson, signature) } returns false

        StepVerifier.create(paymentService.webhook(webhookJson, signature, idempotencyKey))
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    @Test
    fun `webhook should use cached value for idempotent requests`() {
        val idempotencyKey = "idempotent_key"

        every { cacheRepository.get(any(), Boolean::class.java) } returns Mono.just(true)
        StepVerifier.create(paymentService.webhook(TestUtil.orderPaidPayload, TestUtil.orderPaidSignature, idempotencyKey))
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `getOrderId should return order ID for existing product order`() {
        val productOrderId = "prod_order_123"
        val userId = "user_456"
        val paymentEntity = TestUtil.createPaymentEntity()
        every { paymentRepository.getByProductOrderId(productOrderId, userId) } returns Mono.just(paymentEntity)

        StepVerifier.create(paymentService.getOrderId(productOrderId, userId))
            .expectNext(paymentEntity.orderId)
            .verifyComplete()
    }

    @Test
    fun `getOrderId should return empty for non-existent product order`() {
        val productOrderId = "prod_order_non_existent"
        val userId = "user_789"
        every { paymentRepository.getByProductOrderId(productOrderId, userId) } returns Mono.empty()

        StepVerifier.create(paymentService.getOrderId(productOrderId, userId))
            .verifyComplete()
    }
}