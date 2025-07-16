package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.application.util.LoginConstants
import com.ethyllium.authservice.domain.port.driven.CacheRepository
import com.ethyllium.authservice.domain.port.driven.CodeValidator
import com.ethyllium.authservice.domain.port.driven.LoginAttemptRepository
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.domain.util.Constants
import com.ethyllium.authservice.domain.util.Constants.Companion.EMAIL_TOKEN_PREFIX
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class ValidationService(
    private val codeValidator: CodeValidator,
    private val cacheRepository: CacheRepository,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val userRepository: UserRepository,
) {
    fun verifyAccount(token: String): Mono<Boolean> {
        return validateToken(token).flatMap { username ->
            userRepository.findUserByUsername(username).flatMap { user ->
                Mono.zip(
                    userRepository.enableUser(user.username), userRepository.emailVerifiedNow(user.username)
                ).flatMap { tuple ->
                    Mono.just(tuple.t1 > 0 && tuple.t2 > 0)
                }
            }
        }.switchIfEmpty(Mono.just(false))
    }

    fun validateCode(secret: String, code: String): Boolean {
        return codeValidator.validateCode(secret, code)
    }

    private fun validateToken(key: String): Mono<UUID> {
        val token = "$EMAIL_TOKEN_PREFIX$key"
        return cacheRepository.read(token).flatMap {
            val userId = it as String
            cacheRepository.remove(userId).then(Mono.just(UUID.fromString(userId)))
        }.switchIfEmpty(Mono.empty())
    }

    @Transactional
    fun verifyLogin(sessionId: String): Mono<String> {
        return cacheRepository.readHash(Constants.USER_SESSION_PREFIX + sessionId).flatMap { sessionData ->
            if (sessionData.isEmpty()) Mono.empty()
            else {
                val username = sessionData[LoginConstants.MAP_KEY_USERNAME] ?: return@flatMap Mono.empty()
                val deviceFingerprint =
                    sessionData[LoginConstants.MAP_KEY_DEVICE_FINGERPRINT] ?: return@flatMap Mono.empty()
                loginAttemptRepository.addFingerprint(UUID.fromString(username), deviceFingerprint).then(Mono.just(username))
            }
        }
    }
}