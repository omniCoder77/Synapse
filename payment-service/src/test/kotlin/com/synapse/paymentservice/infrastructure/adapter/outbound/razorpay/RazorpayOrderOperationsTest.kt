package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay

import com.razorpay.RazorpayClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class RazorpayOrderOperationsTest {

    private lateinit var razorpayOrderOperations: RazorpayOrderOperations

    @BeforeEach
    fun setUp() {
        razorpayOrderOperations = RazorpayOrderOperations(
            RazorpayClient(
                System.getenv("RAZORPAY_KEY_ID"), System.getenv("RAZORPAY_SECRET_KEY")
            )
        )
    }

    @Test
    fun `createOrderPayment should return success for valid request`() {
        val result = razorpayOrderOperations.createOrderPayment(
            amountInPaise = 1000.0, currency = "INR", receipt = "test_receipt"
        )

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun getOrder_Test() {
        val orderId = "order_QzHMt0FgbENNDL"
        val order = razorpayOrderOperations.getOrder(orderId)
        assertThat(order.isSuccess).isTrue()
    }

    @Test
    fun `createOrderPayment should handle minimum amount correctly`() {
        val order =
            razorpayOrderOperations.createOrderPayment(amountInPaise = 1.0, currency = "INR", receipt = "min_amount")
        assertThat(order.isFailure).isTrue()
    }

    @Test
    fun `createOrderPayment should handle maximum amount correctly`() {
        val order = razorpayOrderOperations.createOrderPayment(
            amountInPaise = 99999999.0, currency = "INR", receipt = "max_amount"
        )
        assertThat(order.isFailure).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["USD", "EUR", "SGD", "AED"])
    fun `createOrderPayment should work with different currencies`(currency: String) {
        val order = razorpayOrderOperations.createOrderPayment(
            amountInPaise = 1000.0, currency = currency, receipt = "multi_currency"
        )
        assertThat(order.isSuccess).isTrue()
    }

    @Test
    fun `createOrderPayment should handle null receipt`() {
        val order = razorpayOrderOperations.createOrderPayment(amountInPaise = 1000.0, currency = "INR", receipt = null)
        assertThat(order.isSuccess).isTrue()
    }

    @Test
    fun `createOrderPayment should handle notes correctly`() {
        val notes = mapOf("key1" to "value1", "key2" to "value2")
        val order = razorpayOrderOperations.createOrderPayment(
            amountInPaise = 1000.0, currency = "INR", receipt = "multi_currency", notes = notes
        )
        assertThat(order.isSuccess).isTrue()
    }

    @Test
    fun `createOrderPayment should throw exception for invalid currency`() {
        val order = razorpayOrderOperations.createOrderPayment(
            amountInPaise = 1000.0, currency = "XYZ", receipt = "invalid_currency"
        )
        assertThat(order.isFailure).isTrue()
    }

    @Test
    fun `createOrderPayment should handle payment capture flag correctly`() {
        val order = razorpayOrderOperations.createOrderPayment(
            amountInPaise = 1000.0, currency = "INR", receipt = "capture_test", paymentCapture = 0
        )
        assertThat(order.isSuccess).isTrue()
    }

    @Test
    fun `getOrder should throw exception for non-existent order`() {
        val order = razorpayOrderOperations.getOrder("non_existent_order_123")
        assertThat(order.isFailure).isTrue()
    }

    @Test
    fun `getOrder should handle all response fields correctly`() {
        val orderId = "order_QyX2SfVs0fb04Y"
        val order = razorpayOrderOperations.getOrder(orderId)
        assertThat(order.isSuccess).isTrue()
        order.onSuccess { order ->
            assertThat(order.id).isEqualTo(orderId)
            assertThat(order.amount).isPositive()
            assertThat(order.currency).isNotBlank()
            assertThat(order.status).isNotBlank()
            assertThat(order.created_at).isBeforeOrEqualTo(Date())
        }
    }
}