package com.ethyllium.authservice.domain.model

import java.util.UUID

class User(
    var password: String,
    val email: String,
    val role: String,
    val username: UUID,
    val isAccountLocked: Boolean = false,
    val isEnabled: Boolean = true,
    val isMfaEnabled: Boolean = false,
    val phoneNumber: String,
)