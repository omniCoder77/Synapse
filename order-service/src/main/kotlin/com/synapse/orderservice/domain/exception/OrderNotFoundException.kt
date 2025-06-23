package com.synapse.orderservice.domain.exception

class OrderNotFoundException(override val message: String): RuntimeException(message)