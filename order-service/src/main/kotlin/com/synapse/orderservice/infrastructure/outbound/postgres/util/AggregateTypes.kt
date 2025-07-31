package com.synapse.orderservice.infrastructure.outbound.postgres.util

/**
 * This is the topic name of kafka.
 */
object AggregateTypes {
    const val ORDER_CREATION_REQUEST = "order_creation_request"
    const val ORDER_CREATED = "order_created"
}