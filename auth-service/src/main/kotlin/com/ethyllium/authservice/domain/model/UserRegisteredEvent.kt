package com.ethyllium.authservice.domain.model

import java.util.*

data class UserRegisteredEvent(
    val userId: UUID,
    val email: String,
    val deviceFingerprint: String,
    val password: String,
    val role: List<Role>,
    val name: String,
    val phoneNumber: String
)