package com.ethyllium.authservice.application.dto.request

import com.ethyllium.authservice.domain.model.User

data class RegisterRequest(
    val name: String,
    val username: String,
    var password: String,
    val email: String,
    val mfa: Boolean,
    val deviceFingerprint: String,
    val phoneNumber: String,
    val role: String
) {
    fun toUser() = User(
        password = this.password,
        username = this.username,
        email = this.email,
        isMfaEnabled = this.mfa,
        role = this.role,
        phoneNumber = this.phoneNumber
    )
}
