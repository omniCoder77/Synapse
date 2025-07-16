package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.dto

data class AuthResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String,
    val refreshToken: String,
)
