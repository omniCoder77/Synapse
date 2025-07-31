package com.synapse.paymentservice.domain.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookEvent(
    @JsonProperty("entity") val entity: String,
    @JsonProperty("account_id") val accountId: String,
    @JsonProperty("event") val event: String,
    @JsonProperty("contains") val contains: List<String>,
    @JsonProperty("payload") val payload: WebhookPayload,
    @JsonProperty("created_at") val createdAt: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WebhookPayload(
    @JsonProperty("payment") val payment: PaymentWrapper? = null,
    @JsonProperty("refund") val refund: RefundWrapper? = null,
    @JsonProperty("order") val order: OrderWrapper? = null
)

/* ========= Payment Related ========= */
data class PaymentWrapper(
    @JsonProperty("entity") val entity: PaymentEntity
)

data class PaymentEntity(
    @JsonProperty("id") val id: String,
    @JsonProperty("entity") val entity: String,
    @JsonProperty("amount") val amount: Int,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("order_id") val orderId: String?,
    @JsonProperty("invoice_id") val invoiceId: String?,
    @JsonProperty("international") val international: Boolean,
    @JsonProperty("method") val method: String,
    @JsonProperty("amount_refunded") val amountRefunded: Int,
    @JsonProperty("refund_status") val refundStatus: String?,
    @JsonProperty("reward") val reward: String?,
    @JsonProperty("captured") val captured: Boolean,
    @JsonProperty("description") val description: String?,
    @JsonProperty("card_id") val cardId: String?,
    @JsonProperty("bank") val bank: String?,
    @JsonProperty("wallet") val wallet: String?,
    @JsonProperty("vpa") val vpa: String?,
    @JsonProperty("email") val email: String,
    @JsonProperty("contact") val contact: String,
    @JsonProperty("notes") val notes: Map<String, String>?,
    @JsonProperty("fee") val fee: Int?,
    @JsonProperty("tax") val tax: Int?,
    @JsonProperty("error_code") val errorCode: String?,
    @JsonProperty("error_description") val errorDescription: String?,
    @JsonProperty("error_source") val errorSource: String?,
    @JsonProperty("error_step") val errorStep: String?,
    @JsonProperty("error_reason") val errorReason: String?,
    @JsonProperty("acquirer_data") val acquirerData: AcquirerData?,
    @JsonProperty("created_at") val createdAt: Long,
    @JsonProperty("upi") val upi: UpiDetails?,
    @JsonProperty("base_amount") val baseAmount: Int?,
    @JsonProperty("amount_transferred") val amountTransferred: Int?
)

data class AcquirerData(
    @JsonProperty("rrn") val rrn: String?,
    @JsonProperty("upi_transaction_id") val upiTransactionId: String?,
    @JsonProperty("transaction_id") val transactionId: String?
)

data class UpiDetails(
    @JsonProperty("vpa") val vpa: String?
)

/* ========= Refund Related ========= */
data class RefundWrapper(
    @JsonProperty("entity") val entity: RefundEntity
)

data class RefundEntity(
    @JsonProperty("id") val id: String,
    @JsonProperty("entity") val entity: String,
    @JsonProperty("amount") val amount: Int,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("payment_id") val paymentId: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("acquirer_data") val acquirerData: RefundAcquirerData?,
    @JsonProperty("batch_id") val batchId: String?,
    @JsonProperty("created_at") val createdAt: Long,
    @JsonProperty("notes") val notes: List<String>?,
    @JsonProperty("receipt") val receipt: String?,
    @JsonProperty("speed_processed") val speedProcessed: String?,
    @JsonProperty("speed_requested") val speedRequested: String?
)

data class RefundAcquirerData(
    @JsonProperty("rrn") val rrn: String?
)

/* ========= Order Related ========= */
data class OrderWrapper(
    @JsonProperty("entity") val entity: OrderEntity
)

data class OrderEntity(
    @JsonProperty("id") val id: String,
    @JsonProperty("entity") val entity: String,
    @JsonProperty("amount") val amount: Int,
    @JsonProperty("amount_paid") val amountPaid: Int,
    @JsonProperty("amount_due") val amountDue: Int,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("receipt") val receipt: String?,
    @JsonProperty("offer_id") val offerId: String?,
    @JsonProperty("status") val status: String,
    @JsonProperty("attempts") val attempts: Int,
    @JsonProperty("notes") val notes: List<String>?,
    @JsonProperty("created_at") val createdAt: Long
)