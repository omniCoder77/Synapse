package com.synapse.orderservice.infrastructure.web.rest.dto

import com.synapse.orderservice.domain.model.Address

data class AddressDTO(
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
) {
    fun toDomain(): Address {
        return Address(
            street = street,
            city = city,
            state = state,
            postalCode = postalCode,
            country = country
        )
    }
}