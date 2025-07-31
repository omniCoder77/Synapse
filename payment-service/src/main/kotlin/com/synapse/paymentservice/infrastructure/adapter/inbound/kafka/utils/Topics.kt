package com.synapse.paymentservice.infrastructure.adapter.inbound.kafka.utils

object Topics {
    const val ORDER_CREATED = "order_created"
    const val PAYMENT_SUCCESS = "payment_success"
    const val PAYMENT_FAILURE = "payment_failure"
    const val PAYMENT_REFUND_FAILURE = "payment_refund_failure"
}