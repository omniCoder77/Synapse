package com.ethyllium.productservice.domain.port.driven

interface EmailService {
    fun sendVerificationEmail(to: String, token: String, expirationMinutes: Int)
}