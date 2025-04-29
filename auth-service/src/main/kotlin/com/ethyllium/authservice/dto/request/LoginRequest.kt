package com.ethyllium.authservice.dto.request

data class LoginRequest(
    val email: String,
    val deviceFingerprint: String
)
