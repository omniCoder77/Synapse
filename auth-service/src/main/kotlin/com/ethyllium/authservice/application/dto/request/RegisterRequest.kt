package com.ethyllium.authservice.application.dto.request

import com.ethyllium.authservice.domain.model.User
import java.util.UUID

data class RegisterRequest(
    val name: String,
    var password: String,
    val email: String,
    val mfa: Boolean,
    val deviceFingerprint: String,
    val phoneNumber: String,
    val role: String
) {
    fun toUser() = User(
        password = this.password,
        username = UUID.randomUUID().toString(),
        email = this.email,
        isMfaEnabled = this.mfa,
        role = this.role,
        phoneNumber = this.phoneNumber
    )
}
