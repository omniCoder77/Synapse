package com.synapse.orderservice.domain.exception

class InvalidProductException(override val message: String): RuntimeException(message)