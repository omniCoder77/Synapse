package com.ethyllium.authservice.application.dto.response

data class AuthResponse(
    val accessToken: String,
    val expiresIn: Long,
    val tokenType: String,
    val refreshToken: String,
)
