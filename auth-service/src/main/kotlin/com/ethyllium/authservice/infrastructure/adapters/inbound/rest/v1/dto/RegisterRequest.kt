package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto

import com.ethyllium.authservice.domain.model.Role
import com.ethyllium.authservice.domain.model.User
import java.util.UUID

data class RegisterRequest(
    val name: String,
    var password: String,
    val email: String,
    val mfa: Boolean,
    val deviceFingerprint: String,
    val phoneNumber: String,
    val role: Role = Role.USER,
    val totp : String? = null
) {
    fun toUser() = User(
        password = this.password,
        username = UUID.randomUUID(),
        email = this.email,
        isMfaEnabled = this.mfa,
        role = listOf(this.role),
        phoneNumber = this.phoneNumber,
        totp = totp
    )
}
