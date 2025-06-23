package com.synapse.orderservice.infrastructure.output.kafka.dto

data class OrderCreated(
    val trackingId: String,
    val userId: String
)
