package com.ethyllium.authservice.domain.port.driven

import reactor.core.publisher.Mono

interface EmailService {
    fun sendVerificationEmail(to: String, token: String, expirationMinutes: Int): Mono<Void>
    fun sendLoginEmail(to: String, sessionId: String, expirationMinutes: Int): Mono<Void>
    fun sendPasswordResetEmail(email: String, resetToken: String, expirationMinutes: Int): Mono<Void>
}
