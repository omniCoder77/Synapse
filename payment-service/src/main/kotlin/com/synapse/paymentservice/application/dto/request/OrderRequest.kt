package com.synapse.paymentservice.application.dto.request

import com.synapse.paymentservice.domain.model.Order

data class OrderRequest(
    val amount: Long,
    val status: OrderStatus,
) {
    fun toOrder() = Order(
        amount = amount, status = status
    )
}

enum class OrderStatus {
    CREATED, PAYMENT_PENDING, PAID, COMPENSATED;

    companion object {
        fun get(get: String): OrderStatus {
            return when (get) {
                PAYMENT_PENDING.name -> PAYMENT_PENDING
                PAID.name -> PAID
                COMPENSATED.name -> COMPENSATED
                "created" -> CREATED
                else -> throw IllegalArgumentException()
            }
        }
    }
}