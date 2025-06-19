package com.ethyllium.authservice.domain.port.driven

interface EmailService {
    fun sendVerificationEmail(to: String, token: String, expirationMinutes: Int)
    fun sendLoginEmail(to: String, sessionId: String, expirationMinutes: Int)
    fun sendPasswordResetEmail(email: String, resetToken: String, expirationMinutes: Int)
}
