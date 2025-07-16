package com.ethyllium.authservice.domain.port.driver

import com.ethyllium.authservice.domain.model.LoginAttempt
import reactor.core.publisher.Mono

interface LoginUseCase {
    fun login(email: String, password: String, deviceFingerprint: String, isMfaLogin: Boolean): Mono<LoginAttempt>
}