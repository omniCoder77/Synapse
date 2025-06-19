package com.ethyllium.authservice.application.dto.request

data class LoginRequest(
    val email: String,
    val deviceFingerprint: String,
    val password: String
)
