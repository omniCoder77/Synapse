package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.port.driven.CacheRepository
import com.ethyllium.authservice.domain.port.driven.CodeValidator
import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.domain.util.Constants
import com.ethyllium.authservice.domain.util.Constants.Companion.EMAIL_TOKEN_PREFIX
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaLoginAttemptRepository
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ValidationService(
    private val jpaUserEntityRepository: JpaUserEntityRepository,
    private val codeValidator: CodeValidator,
    private val cacheRepository: CacheRepository,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val jpaLoginAttemptRepository: JpaLoginAttemptRepository,
) {

    @Transactional
    fun verifyAccount(token: String): Boolean {
        val userId = validateToken(token) ?: return false
        val user = jpaUserEntityRepository.findById(userId).orElse(null) ?: return false
        jpaUserEntityRepository.enableUser(user.username)
        jpaUserEntityRepository.verifyEmailNow(user.username)
        return true
    }

    fun validateCode(secret: String, code: String): Boolean {
        return codeValidator.validateCode(secret, code)
    }

    private fun validateToken(key: String): String? {
        val token = "$EMAIL_TOKEN_PREFIX$key"
        val userId = cacheRepository.read(token) ?: return null
        cacheRepository.remove(token)
        return userId as String
    }

    @Transactional
    fun verifyLogin(sessionId: String): String? {
        val sessionData = cacheRepository.readHash(Constants.USER_SESSION_PREFIX + sessionId) ?: return null
        val username = sessionData[LoginService.MAP_KEY_USERNAME] ?: return null
        val deviceFingerprint = sessionData[LoginService.MAP_KEY_DEVICE_FINGERPRINT] ?: return null
        loginAttemptRepository.addFingerprint(username, deviceFingerprint)
        return username
    }
}