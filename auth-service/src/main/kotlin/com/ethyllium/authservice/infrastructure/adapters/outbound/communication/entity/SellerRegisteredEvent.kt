package com.ethyllium.authservice.infrastructure.adapters.outbound.communication.entity

data class SellerRegisteredEvent(
    val userId: String,
    val email: String,
    val role: List<String>,
    val phoneNumber: String,
    val name: String
)