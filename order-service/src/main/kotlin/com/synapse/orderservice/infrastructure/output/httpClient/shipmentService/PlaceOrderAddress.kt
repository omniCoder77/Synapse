package com.synapse.orderservice.infrastructure.output.httpClient.shipmentService

import com.synapse.orderservice.domain.model.Address

data class PlaceOrderAddress(
    val addressLine1: String,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val city: String,
    val zipCode: String,
    val country: String = "India",
    val postalCode: String,
)

fun Address.toPlaceOrderAddress() = PlaceOrderAddress(
    this.addressLine1,
    this.addressLine2,
    this.addressLine3,
    this.city,
    this.zipCode,
    this.country,
    this.postalCode
)