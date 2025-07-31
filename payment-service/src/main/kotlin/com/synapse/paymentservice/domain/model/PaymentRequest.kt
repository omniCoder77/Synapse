package com.synapse.paymentservice.domain.model

import java.util.*

data class PaymentRequest(
    val productOrderId: UUID,
    val amount: Double,
    val status: PaymentStatus,
)

enum class PaymentStatus {
    CREATED, PAID, FAILED;

    companion object {
        fun get(get: String): PaymentStatus {
            return when (get.lowercase()) {
                PAID.name.lowercase() -> PAID
                CREATED.name.lowercase() -> CREATED
                else -> throw IllegalArgumentException("Unknown order status: $get")
            }
        }
    }
}