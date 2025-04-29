package com.synapse.orderservice.infrastructure.output.persistence.jpa

import com.synapse.orderservice.domain.model.Address
import jakarta.persistence.Embeddable

@Embeddable
data class AddressEmbeddedEntity(
    val addressLine1: String,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val city: String,
    val zipCode: String,
    val country: String = "India",
    val postalCode: String,
) {
    fun toAddress() = Address(
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        addressLine3 = addressLine3,
        city = city,
        zipCode = zipCode,
        country = country,
        postalCode = postalCode,
    )
}

fun Address.toAddressEmbeddedEntity() = AddressEmbeddedEntity(
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    addressLine3 = addressLine3,
    city = city,
    zipCode = zipCode,
    country = country,
    postalCode = postalCode
)