package com.synapse.paymentservice.util

import com.synapse.paymentservice.domain.model.*
import com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.utils.Topics
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.Outbox
import com.synapse.paymentservice.infrastructure.adapter.outbound.persistence.postgres.entity.PaymentEntity
import java.util.*
import com.synapse.paymentservice.domain.model.OrderEntity as WebhookOrderEntity
import com.synapse.paymentservice.domain.model.PaymentEntity as WebhookPaymentEntity
import com.synapse.paymentservice.domain.model.RefundEntity as WebhookRefundEntity

object TestUtil {

    fun createPayment(
        id: UUID = UUID.randomUUID(), productOrderId: UUID = UUID.randomUUID(), userId: String = "test-user"
    ) = Payment(
        id = id,
        amount = 5000L,
        status = PaymentStatus.CREATED,
        orderId = "order_${UUID.randomUUID()}",
        receipt = "receipt_${UUID.randomUUID()}",
        productOrderId = productOrderId,
        userId = userId
    )

    fun createPaymentEntity(
        id: UUID = UUID.randomUUID(), productOrderId: UUID = UUID.randomUUID(), userId: String = "test-user"
    ) = PaymentEntity(
        id = id,
        amountInSmallestDimension = 5000L,
        orderId = "order_${UUID.randomUUID()}",
        productOrderId = productOrderId,
        userId = userId,
        status = "CREATED"
    )

    fun createOutbox(
        aggregateId: UUID = UUID.randomUUID(), eventType: String = "order.paid"
    ) = Outbox(
        aggregateId = aggregateId, aggregateType = Topics.PAYMENT_SUCCESS, eventType = eventType, payload = "{}"
    )

    val paymentFailedPayload = "{\"entity\":\"event\",\"account_id\":\"acc_OJonInJoIWHhhk\",\"event\":\"payment.failed\",\"contains\":[\"payment\"],\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_Qzm1xOMdWftGM1\",\"entity\":\"payment\",\"amount\":1000,\"currency\":\"INR\",\"status\":\"failed\",\"order_id\":\"order_QzOvch43XPITzu\",\"invoice_id\":null,\"international\":false,\"method\":\"wallet\",\"amount_refunded\":0,\"refund_status\":null,\"captured\":false,\"description\":\"Test Transaction\",\"card_id\":null,\"bank\":null,\"wallet\":\"freecharge\",\"vpa\":null,\"email\":\"gaurav.kumar@example.com\",\"contact\":\"+919876543210\",\"notes\":{\"address\":\"Razorpay Corporate Office\"},\"fee\":null,\"tax\":null,\"error_code\":\"BAD_REQUEST_ERROR\",\"error_description\":\"Your payment has been cancelled. Try again or complete the payment later.\",\"error_source\":\"customer\",\"error_step\":\"payment_authentication\",\"error_reason\":\"payment_cancelled\",\"acquirer_data\":{\"transaction_id\":null},\"created_at\":1753986546}}},\"created_at\":1753986550}"
    val paymentFailedSignature = "c96095dc8ada2c5ade6a596b113ee9f883c140cbfc422bd85d15d03c39f1310b"
    val paymentFailedEventId = "Qzm22s8ZDguPGk"

    val orderPaidPayload = "{\"entity\":\"event\",\"account_id\":\"acc_OJonInJoIWHhhk\",\"event\":\"order.paid\",\"contains\":[\"payment\",\"order\"],\"payload\":{\"payment\":{\"entity\":{\"id\":\"pay_Qzm2MzWqa8Pmf4\",\"entity\":\"payment\",\"amount\":1000,\"currency\":\"INR\",\"status\":\"captured\",\"order_id\":\"order_QzOvch43XPITzu\",\"invoice_id\":null,\"international\":false,\"method\":\"upi\",\"amount_refunded\":0,\"refund_status\":null,\"captured\":true,\"description\":\"Test Transaction\",\"card_id\":null,\"bank\":null,\"wallet\":null,\"vpa\":\"kk@icici\",\"email\":\"gaurav.kumar@example.com\",\"contact\":\"+919876543210\",\"notes\":{\"address\":\"Razorpay Corporate Office\"},\"fee\":24,\"tax\":4,\"error_code\":null,\"error_description\":null,\"error_source\":null,\"error_step\":null,\"error_reason\":null,\"acquirer_data\":{\"rrn\":\"276448108305\",\"upi_transaction_id\":\"507862637792F9092AE422E8B52D5F76\"},\"created_at\":1753986569,\"reward\":null,\"upi\":{\"vpa\":\"kk@icici\"}}},\"order\":{\"entity\":{\"id\":\"order_QzOvch43XPITzu\",\"entity\":\"order\",\"amount\":1000,\"amount_paid\":1000,\"amount_due\":0,\"currency\":\"INR\",\"receipt\":\"test_receipt\",\"offer_id\":null,\"status\":\"paid\",\"attempts\":2,\"notes\":[],\"created_at\":1753905188}}},\"created_at\":1753986570}"
    val orderPaidSignature = "56de36699094cb80ab74285776632a9c3eecc8a3e2a199e9b44e3ed568e5f61d"
    val orderPaidEventId = "Qzm2Ov6S2MaOvd"

    val refundCreatedPayload = "{\"account_id\":\"acc_OJonInJoIWHhhk\",\"contains\":[\"refund\",\"payment\"],\"created_at\":1753986615,\"entity\":\"event\",\"event\":\"refund.created\",\"payload\":{\"payment\":{\"entity\":{\"acquirer_data\":{\"rrn\":\"276448108305\",\"upi_transaction_id\":\"507862637792F9092AE422E8B52D5F76\"},\"amount\":1000,\"amount_refunded\":1000,\"amount_transferred\":0,\"bank\":null,\"base_amount\":1000,\"captured\":true,\"card_id\":null,\"contact\":\"+919876543210\",\"created_at\":1753986569,\"currency\":\"INR\",\"description\":\"Test Transaction\",\"email\":\"gaurav.kumar@example.com\",\"entity\":\"payment\",\"error_code\":null,\"error_description\":null,\"error_reason\":null,\"error_source\":null,\"error_step\":null,\"fee\":24,\"id\":\"pay_Qzm2MzWqa8Pmf4\",\"international\":false,\"invoice_id\":null,\"method\":\"upi\",\"notes\":{\"address\":\"Razorpay Corporate Office\"},\"order_id\":\"order_QzOvch43XPITzu\",\"refund_status\":\"full\",\"status\":\"refunded\",\"tax\":4,\"upi\":{\"vpa\":\"kk@icici\"},\"vpa\":\"kk@icici\",\"wallet\":null}},\"refund\":{\"entity\":{\"acquirer_data\":{\"rrn\":\"10000000000000\"},\"amount\":1000,\"batch_id\":null,\"created_at\":1753986608,\"currency\":\"INR\",\"entity\":\"refund\",\"id\":\"rfnd_Qzm33nUMqysUAL\",\"notes\":[],\"payment_id\":\"pay_Qzm2MzWqa8Pmf4\",\"receipt\":null,\"speed_processed\":\"normal\",\"speed_requested\":\"normal\",\"status\":\"processed\"}}}}"
    val refundCreatedSignature = "ad91042bcb24a5544bb7c53457bb96ed64949973d2f61b465c0d85f53198bc26"
    val refundCreatedEventId = "Qzm3btQHmDqhFv"

    val refundProcessedPayload = "{\"account_id\":\"acc_OJonInJoIWHhhk\",\"contains\":[\"refund\",\"payment\"],\"created_at\":1753986615,\"entity\":\"event\",\"event\":\"refund.processed\",\"payload\":{\"payment\":{\"entity\":{\"acquirer_data\":{\"rrn\":\"276448108305\",\"upi_transaction_id\":\"507862637792F9092AE422E8B52D5F76\"},\"amount\":1000,\"amount_refunded\":1000,\"amount_transferred\":0,\"bank\":null,\"base_amount\":1000,\"captured\":true,\"card_id\":null,\"contact\":\"+919876543210\",\"created_at\":1753986569,\"currency\":\"INR\",\"description\":\"Test Transaction\",\"email\":\"gaurav.kumar@example.com\",\"entity\":\"payment\",\"error_code\":null,\"error_description\":null,\"error_reason\":null,\"error_source\":null,\"error_step\":null,\"fee\":24,\"id\":\"pay_Qzm2MzWqa8Pmf4\",\"international\":false,\"invoice_id\":null,\"method\":\"upi\",\"notes\":{\"address\":\"Razorpay Corporate Office\"},\"order_id\":\"order_QzOvch43XPITzu\",\"refund_status\":\"full\",\"status\":\"refunded\",\"tax\":4,\"upi\":{\"vpa\":\"kk@icici\"},\"vpa\":\"kk@icici\",\"wallet\":null}},\"refund\":{\"entity\":{\"acquirer_data\":{\"rrn\":\"10000000000000\"},\"amount\":1000,\"batch_id\":null,\"created_at\":1753986608,\"currency\":\"INR\",\"entity\":\"refund\",\"id\":\"rfnd_Qzm33nUMqysUAL\",\"notes\":[],\"payment_id\":\"pay_Qzm2MzWqa8Pmf4\",\"receipt\":null,\"speed_processed\":\"normal\",\"speed_requested\":\"normal\",\"status\":\"processed\"}}}}"
    val refundProcessedSignature = "dcd9aaaea4846b572d667528b5e4a2ac460e70835abd5b7851d19c6449f1998d"
    val refundProcessedEventId = "Qzm3bujke1TPuS"

    fun createWebhookPayload(event: String): WebhookEvent {
        val orderId = "order_${UUID.randomUUID()}"
        val paymentId = "pay_${UUID.randomUUID()}"
        val refundId = "rfnd_${UUID.randomUUID()}"

        val paymentEntity = WebhookPaymentEntity(
            id = paymentId,
            entity = "payment",
            amount = 5000,
            currency = "INR",
            status = "captured",
            orderId = orderId,
            email = "test@example.com",
            contact = "9999999999",
            notes = emptyMap(),
            createdAt = System.currentTimeMillis() / 1000L,
            international = false,
            method = "card",
            amountRefunded = 0,
            captured = true,
            acquirerData = null,
            bank = null,
            cardId = null,
            description = null,
            errorCode = null,
            errorDescription = null,
            errorReason = null,
            errorSource = null,
            errorStep = null,
            fee = null,
            invoiceId = null,
            refundStatus = null,
            tax = null,
            upi = null,
            vpa = null,
            wallet = null,
            baseAmount = null,
            amountTransferred = null,
            reward = null
        )

        val orderEntity = WebhookOrderEntity(
            id = orderId,
            entity = "order",
            amount = 5000,
            amountPaid = 5000,
            amountDue = 0,
            currency = "INR",
            receipt = "receipt_test",
            status = "paid",
            attempts = 1,
            notes = emptyList(),
            createdAt = System.currentTimeMillis() / 1000L,
            offerId = null
        )

        val refundEntity = WebhookRefundEntity(
            id = refundId,
            entity = "refund",
            amount = 1000,
            currency = "INR",
            paymentId = paymentId,
            status = "processed",
            acquirerData = null,
            batchId = null,
            createdAt = System.currentTimeMillis() / 1000L,
            notes = emptyList(),
            receipt = null,
            speedProcessed = "normal",
            speedRequested = "normal"
        )


        val payload = WebhookPayload(
            payment = PaymentWrapper(paymentEntity),
            order = OrderWrapper(orderEntity),
            refund = RefundWrapper(refundEntity)
        )

        return WebhookEvent(
            entity = "event",
            accountId = "acc_test",
            event = event,
            contains = listOf("payment", "order"),
            payload = payload,
            createdAt = System.currentTimeMillis() / 1000L
        )
    }
}