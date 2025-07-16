package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.dto

import com.ethyllium.authservice.domain.model.User
import java.util.UUID

data class RegisterRequest(
    val name: String,
    var password: String,
    val email: String,
    val mfa: Boolean,
    val deviceFingerprint: String,
    val phoneNumber: String,
    val role: String = "MEMBER"
) {
    fun toUser() = User(
        password = this.password,
        username = UUID.randomUUID(),
        email = this.email,
        isMfaEnabled = this.mfa,
        role = this.role,
        phoneNumber = this.phoneNumber
    )
}
