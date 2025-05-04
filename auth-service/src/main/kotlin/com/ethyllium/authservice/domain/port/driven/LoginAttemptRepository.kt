package com.ethyllium.authservice.domain.port.driven

import com.ethyllium.authservice.infrastructure.persistence.jpa.LoginAttemptEntity

interface LoginAttemptRepository {
    fun save(loginAttempt: LoginAttemptEntity)
}