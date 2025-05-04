package com.ethyllium.authservice.domain.model

class User(
    var password: String,
    var username: String,
    val email: String,
    val role: String,
    val isAccountLocked: Boolean = false,
    val isEnabled: Boolean = true,
    val isMfaEnabled: Boolean = false,
    val phoneNumber: String,
)