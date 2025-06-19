package com.ethyllium.authservice.domain.port.driven

import com.ethyllium.authservice.infrastructure.persistence.jpa.LoginAttemptEntity

interface LoginAttemptRepository {
    fun save(loginAttempt: LoginAttemptEntity)
    fun getFingerprints(username: String): MutableList<String>
    fun addFingerprint(username: String, deviceFingerprint: String)
    fun resetAttempt(username: String)
}