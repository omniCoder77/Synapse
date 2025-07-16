package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto

data class AuthResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String,
    val refreshToken: String,
)
