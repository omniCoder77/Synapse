package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto

data class PasswordResetRequest(val email: String)
data class ValidateTokenRequest(val email: String, val token: String)
data class NewPasswordRequest(val newPassword: String, val confirmPassword: String)
data class PasswordResetResponse(
    val mfaRequired: Boolean,
    val sessionToken: String? = null,
    val resetToken: String? = null,
    val mfaTypes: List<String>? = null
)

data class PasswordResetTokenResponse(val resetToken: String)
