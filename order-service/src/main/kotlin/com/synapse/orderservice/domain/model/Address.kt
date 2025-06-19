package com.synapse.orderservice.domain.model

data class Address(
    val addressLine1: String,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val city: String,
    val zipCode: String,
    val country: String = "India",
    val postalCode: String,
)
