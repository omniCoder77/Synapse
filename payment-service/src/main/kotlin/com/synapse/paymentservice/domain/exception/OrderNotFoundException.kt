package com.synapse.paymentservice.domain.exception

class OrderNotFoundException(orderId: String): RuntimeException("Order with id [$orderId] does not exist")