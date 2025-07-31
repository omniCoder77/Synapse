package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request parameters for processing a refund through Razorpay API.
 *
 * @property amountInPaise The amount to be refunded in the smallest unit of the currency.
 *                  For three decimal currencies (KWD, BHD, OMR), to refund 295.991, pass 295990.
 *                  For zero decimal currencies (JPY), to refund 295, pass 295.
 *                  For partial refunds, pass a value less than the payment amount.
 *                  If not provided, the entire payment amount will be refunded.
 * @property paymentId The ID of the payment to be refunded.
 * @property speed The processing speed for the refund. Default is "normal".
 *                 Normal speed processes refunds within 5-7 working days.
 * @property notes Additional key-value pairs for storing extra information.
 *                 Maximum of 15 key-value pairs allowed.
 * @property receipt A unique identifier for internal reference purposes.
 * @property reverseAll Whether to reverse all associated transactions. Default is false.
 */

data class RefundRequest(
    @JsonProperty("amount") val amountInPaise: Int,

    @JsonProperty("payment_id") val paymentId: String,

    @JsonProperty("speed") val speed: String = "normal",

    @JsonProperty("notes") val notes: Map<String, String>? = null,

    @JsonProperty("receipt") val receipt: String? = null,

    @JsonProperty("reverse_all") val reverseAll: Boolean? = false
)