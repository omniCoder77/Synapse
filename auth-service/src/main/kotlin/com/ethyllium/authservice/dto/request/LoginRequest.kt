package com.ethyllium.authservice.dto.request

data class LoginRequest(
    val username: String,
    val deviceFingerprint: String
)