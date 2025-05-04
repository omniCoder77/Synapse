package com.ethyllium.authservice.domain.port.driven

interface EmailService {
    fun sendVerificationEmail(to: String, token: String)
}
