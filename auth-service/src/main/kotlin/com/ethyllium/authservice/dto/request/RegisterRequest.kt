package com.ethyllium.authservice.dto.request

import com.ethyllium.authservice.model.User

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val f2a: Boolean,
    val deviceFingerprint: String
)