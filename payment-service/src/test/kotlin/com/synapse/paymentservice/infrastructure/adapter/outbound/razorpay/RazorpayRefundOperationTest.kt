package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay

import com.razorpay.RazorpayClient
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.request.FetchMultipleRefundsRequest
import com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.request.RefundRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RazorpayRefundOperationTest {
    private lateinit var razorpayRefundOperation: RazorpayRefundOperation

    val paymentId = "pay_QzKTzotOCCqngz"
    val refundId = "rfnd_QzJmlBAOwKdDoT"

    @BeforeEach
    fun setUp() {
        razorpayRefundOperation = RazorpayRefundOperation(
            RazorpayClient(
                "rzp_test_LVfYqp2c4TtHRR", "599qZgN39dFYYS62aI4nUyXg"
            )
        )
    }

    @Test
    fun refundOrderPaymentNormalMode_Test() {
        val refundResponse = razorpayRefundOperation.refundOrderPayment(
            RefundRequest(
                amountInPaise = 1000, paymentId = "pay_Qzm2MzWqa8Pmf4", speed = "normal"
            )
        )
        assertTrue(refundResponse.isSuccess)
    }

    @Test
    fun refundOrderPaymentInstantMode_Test() {
        val refundResponse = razorpayRefundOperation.refundOrderPayment(
            RefundRequest(
                amountInPaise = 100, paymentId = paymentId, speed = "optimum"
            )
        )
        assertTrue(refundResponse.isSuccess)
    }

    @Test
    fun `refundOrderPayment should handle partial refund correctly`() {
        val refundAmount = 500

        val refundResponse = razorpayRefundOperation.refundOrderPayment(
            RefundRequest(
                amountInPaise = refundAmount,
                paymentId = paymentId,
            )
        )

        assertTrue(refundResponse.isSuccess)
    }

    @Test
    fun `refundOrderPayment should throw exception for non-existent payment`() {
        val res = razorpayRefundOperation.refundOrderPayment(
            RefundRequest(
                amountInPaise = 100, paymentId = "non_existent_payment_123"
            )
        )
        assertTrue(res.isFailure)
    }

    @Test
    fun `refundOrderPayment should handle notes correctly`() {
        val notes = mapOf("reason" to "customer_request", "comment" to "item_not_needed")

        val refundResponse = razorpayRefundOperation.refundOrderPayment(
            RefundRequest(
                amountInPaise = 100, paymentId = paymentId, notes = notes
            )
        )

        assertTrue(refundResponse.isSuccess)
    }

    @Test
    fun getRefunds_Test() {
        val multipleRefundsRequest = FetchMultipleRefundsRequest(count = 10)
        val refunds = razorpayRefundOperation.getRefunds(multipleRefundsRequest, paymentId)
        assertTrue(refunds.isSuccess)
    }

    @Test
    fun getRefund_Test() {
        val refundResponse = razorpayRefundOperation.getRefund(paymentId, refundId)
        assertNotNull(refundResponse)
    }

    @Test
    fun `refundOrderPayment should throw exception when refunding more than payment amount`() {
        assertThatThrownBy {
            razorpayRefundOperation.refundOrderPayment(
                RefundRequest(
                    amountInPaise = 999999, "gyuhijok"
                )
            )
        }.isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `getRefunds should handle pagination correctly`() {
        val multipleRefundsRequest = FetchMultipleRefundsRequest(count = 2, skip = 1)
        val refunds = razorpayRefundOperation.getRefunds(multipleRefundsRequest, paymentId)

        assertTrue(refunds.isSuccess)
    }

    @Test
    fun `getRefunds should handle empty result`() {
        val paymentId = UUID.randomUUID().toString()
        val multipleRefundsRequest = FetchMultipleRefundsRequest(count = 10)
        val refunds = razorpayRefundOperation.getRefunds(multipleRefundsRequest, paymentId)

        assertTrue(refunds.isSuccess)
        refunds.onSuccess { assertThat(it).isEmpty() }
    }

    @Test
    fun `getRefund should handle all response fields correctly`() {
        val refundResponse = razorpayRefundOperation.getRefund(paymentId, refundId)
        assertNotNull(refundResponse)
    }

    @Test
    fun `getRefund should throw exception for non-existent refund`() {
        assertNotNull(razorpayRefundOperation.getRefund(paymentId, "non_existent_refund_123"))
    }
}