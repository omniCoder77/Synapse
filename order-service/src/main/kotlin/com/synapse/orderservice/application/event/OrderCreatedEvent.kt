package com.synapse.orderservice.application.event

data class OrderCreatedEvent(val total: Double, val currency: String)