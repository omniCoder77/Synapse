package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Represents a refund response from Razorpay API.
 *
 * @property id The unique identifier of the refund (e.g., rfnd_FgRAHdNOM4ZVbO)
 * @property paymentId The unique identifier of the payment for which a refund is initiated (e.g., pay_FgR9UMzgmKDJRi)
 * @property amount The amount to be refunded in the smallest unit of currency (e.g., ₹30.00 = 3000)
 * @property currency The currency of payment amount for which the refund is initiated
 * @property notes Key-value store for storing reference data. Maximum of 15 key-value pairs
 * @property receipt A unique identifier provided for internal reference
 * @property status Indicates the state of the refund. Possible values: "processed", "pending", "failed"
 * @property speedProcessed The mode used to process the refund. Values: "instant", "normal"
 * @property speedRequested The processing mode requested for the refund. Values: "normal", "optimum"
 * @property createdAt Unix timestamp at which the refund was created
 * @property acquirerData A dynamic array consisting of a unique reference number (either RRN, ARN or UTR) that is provided by the banking partner when a refund is processed.
 */

data class RefundResponse(
    @JsonProperty("id") val id: String,

    @JsonProperty("payment_id") val paymentId: String,

    @JsonProperty("amount") val amount: Long,

    @JsonProperty("currency") val currency: String,

    @JsonProperty("notes") val notes: Map<String, String>?,

    @JsonProperty("receipt") val receipt: String?,

    @JsonProperty("status") val status: String,

    @JsonProperty("speed_processed") val speedProcessed: String,

    @JsonProperty("speed_requested") val speedRequested: String,

    @JsonProperty("created_at") val createdAt: Date,

    @JsonProperty("acquirer_data") val acquirerData: AcquirerData?
)