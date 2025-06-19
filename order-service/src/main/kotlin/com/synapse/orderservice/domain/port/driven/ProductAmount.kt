package com.synapse.orderservice.domain.port.driven

interface ProductAmount {
    fun calculateAmount(productId: String): Double?
}