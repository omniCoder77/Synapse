package com.ethyllium.authservice.dto.request

data class RegisterRequest(
    val name: String,

    val username: String, val password: String, val email: String, val f2a: Boolean, val deviceFingerprint: String
)
