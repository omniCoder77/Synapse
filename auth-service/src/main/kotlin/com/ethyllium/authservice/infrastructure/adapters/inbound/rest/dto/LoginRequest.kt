package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.dto

data class LoginRequest(
    val email: String,
    val deviceFingerprint: String,
    val password: String
)
