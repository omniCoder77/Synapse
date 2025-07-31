package com.synapse.paymentservice.infrastructure.adapter.outbound.postgres

import com.synapse.paymentservice.domain.model.Payment
import com.synapse.paymentservice.domain.model.PaymentRequest
import com.synapse.paymentservice.domain.model.PaymentStatus
import com.synapse.paymentservice.domain.port.driven.PaymentRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.util.*

@SpringBootTest
class PaymentRepositoryImplTest {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    @Test
    @Order(1)
    fun save_Test() {
        val payment = Payment(
            amount = 100,
            status = PaymentStatus.CREATED,
            orderId = UUID.randomUUID().toString(),
            receipt = UUID.randomUUID().toString(),
            productOrderId = UUID.randomUUID(),
            userId = "test-user-id"
        )
        paymentRepository.save(payment, payment.productOrderId).`as`(StepVerifier::create).assertNext { actual ->
            Assertions.assertThat(actual.id).isNotNull
        }.verifyComplete()
    }

    @Test
    @Order(2)
    fun createPaymentOrder_Test() {
        val paymentRequest =
            PaymentRequest(productOrderId = UUID.randomUUID(), amount = 100.0, status = PaymentStatus.CREATED)
        paymentRepository.createPaymentOrder(
            paymentRequest,
            ""
        ).`as`(StepVerifier::create).assertNext { actual ->
            Assertions.assertThat(actual.paymentStatus).isEqualTo(PaymentStatus.CREATED)
        }.verifyComplete()
    }

    @Test
    fun save_DuplicateRazorpayOrderId_Test() {
        val payment1 =Payment(
            amount = 100,
            status = PaymentStatus.CREATED,
            orderId = UUID.randomUUID().toString(),
            receipt = UUID.randomUUID().toString(),
            productOrderId = UUID.randomUUID(),
            userId = "test-user-id"
        )

        val payment2 = Payment(
            amount = 100,
            status = PaymentStatus.CREATED,
            orderId = UUID.randomUUID().toString(),
            receipt = UUID.randomUUID().toString(),
            productOrderId = UUID.randomUUID(),
            userId = "test-user-id"
        )


        paymentRepository.save(payment1, payment1.productOrderId).block()
        paymentRepository.save(payment2, payment2.productOrderId).`as`(StepVerifier::create).assertNext { actual ->
            Assertions.assertThat(actual.amount).isEqualTo(200.0)
            Assertions.assertThat(actual.status).isEqualTo(PaymentStatus.PAID)
        }.verifyComplete()
    }

    @Test
    fun findById_NonExistentId_Test() {
        val nonExistentId = UUID.randomUUID().toString()
        paymentRepository.findById(nonExistentId).`as`(StepVerifier::create).verifyComplete()
    }

    @Test
    fun findById_Test() {
        val orderId = "ffcf59f4-5f9e-4f5b-9106-18ea4cd2522e"
        paymentRepository.findById(orderId).`as`(StepVerifier::create).assertNext { actual ->
            Assertions.assertThat(actual.id.toString()).isEqualTo(orderId)
        }.verifyComplete()
    }

    @Test
    fun findByRazorpayOrderId_Test() {
        val razorpayId = "c24e3367-f5bc-4942-8a02-0ef6b21a670f"
        paymentRepository.findByRazorpayOrderId(razorpayId).`as`(StepVerifier::create).assertNext { actual ->
            Assertions.assertThat(actual.orderId).isEqualTo(razorpayId)
        }.verifyComplete()
    }

    @Test
    fun paid_Test() {
        val razorpayId = "c24e3367-f5bc-4942-8a02-0ef6b21a670f"
        paymentRepository.paid(razorpayId).`as`(StepVerifier::create).assertNext { actual ->
            org.junit.jupiter.api.Assertions.assertTrue(actual)
        }.verifyComplete()
    }

    @Test
    fun findByRazorpayOrderId_NonExistentId_Test() {
        val nonExistentRazorpayOrderId = UUID.randomUUID().toString()
        paymentRepository.findByRazorpayOrderId(nonExistentRazorpayOrderId).`as`(StepVerifier::create).verifyComplete()
    }

    @Test
    fun paid_NonExistentRazorpayOrderId_Test() {
        val nonExistentRazorpayOrderId = UUID.randomUUID().toString()
        paymentRepository.paid(nonExistentRazorpayOrderId).`as`(StepVerifier::create).assertNext { actual ->
            org.junit.jupiter.api.Assertions.assertFalse(actual)
        }.verifyComplete()
    }

    @Test
    fun save_NullOrEmptyFields_Test() {
        val payment = Payment(
            amount = 100,
            status = PaymentStatus.CREATED,
            orderId = UUID.randomUUID().toString(),
            receipt = UUID.randomUUID().toString(),
            productOrderId = UUID.randomUUID(),
            userId = "test-user-id"
        )

        paymentRepository.save(payment, payment.productOrderId).`as`(StepVerifier::create).assertNext { actual ->
            Assertions.assertThat(actual.orderId).isEmpty()
            Assertions.assertThat(actual.receipt).isEmpty()
        }.verifyComplete()
    }
}