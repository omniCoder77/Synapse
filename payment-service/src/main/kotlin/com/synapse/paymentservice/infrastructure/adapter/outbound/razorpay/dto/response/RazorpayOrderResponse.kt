package com.synapse.paymentservice.infrastructure.adapter.outbound.razorpay.dto.response

import java.util.*

/**
 * Response entity for Razorpay Order Fetch API
 * @see <a href="https://razorpay.com/docs/api/orders/fetch-with-id/">Razorpay Order Fetch Documentation</a>
 */
data class RazorpayOrderResponse(
    /**
     * The unique identifier of the order
     * @example "order_IAY6mKdijmJk9l"
     */
    val id: String,

    /**
     * The order amount in the smallest currency unit (e.g., paise for INR)
     * @example 50000
     */
    val amount: Long,

    /**
     * The amount paid against the order
     * @example 0
     */
    val amount_paid: Long,

    /**
     * The amount due against the order
     * @example 50000
     */
    val amount_due: Long,

    /**
     * The currency of the payment (ISO currency code)
     * @example "INR"
     */
    val currency: String,

    /**
     * The receipt number associated with the order
     * @example "receipt_42"
     */
    val receipt: String?,

    /**
     * The status of the order
     * @example "created"
     * @enum ["created", "attempted", "paid"]
     */
    val status: String,

    /**
     * The number of payment attempts made against the order
     * @example 0
     */
    val attempts: Int,

    /**
     * Notes associated with the order in key-value pairs
     * @example {"notes_key_1":"Tea, Earl Grey, Hot","notes_key_2":"Tea, Earl Grey… decaf."}
     */
    val notes: Map<String, String>?,

    /**
     * Unix timestamp when the order was created
     * @example 1655896199
     */
    val created_at: Date
)