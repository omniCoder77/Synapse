package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.port.driven.*
import com.ethyllium.authservice.domain.util.Constants
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaLoginAttemptRepository
import com.ethyllium.authservice.infrastructure.persistence.jpa.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

@Service
@EnableAsync
@Transactional
class LoginService(
    private val userRepository: UserRepository,
    private val jpaLoginAttemptRepository: JpaLoginAttemptRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val emailService: EmailService,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val cacheRepository: CacheRepository,
    @Value("\${mail.login.verification-min}") private val verificationEmailExpirationMinutes: Int,
) {

    companion object {
        const val MAP_KEY_USERNAME = "username"
        const val MAP_KEY_DEVICE_FINGERPRINT = "device_fingerprint"
    }

    fun login(email: String, password: String, deviceFingerprint: String, isMfaLogin: Boolean): LoginAttempt {
        val user = userRepository.findByEmail(email) ?: throw UsernameNotFoundException(email)

        if (!isPasswordValid(password, user.password)) {
            return LoginAttempt.InvalidCredentials
        }
        val fingerprints = loginAttemptRepository.getFingerprints(user.username)
        val isKnownDevice = fingerprints.contains(deviceFingerprint)

        if (!isKnownDevice) {
            return handleNewDevice(user, deviceFingerprint, isMfaLogin)
        }

        return if (user.mfa && !isMfaLogin) {
            LoginAttempt.MFALogin
        } else {
            val accessToken = tokenService.generateAccessToken(user.username)
            jpaLoginAttemptRepository.resetAttempt(user.username)
            LoginAttempt.Success(accessToken)
        }
    }

    private fun isPasswordValid(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    private fun handleNewDevice(user: UserEntity, deviceFingerprint: String, isMfaLogin: Boolean): LoginAttempt {
        if (isMfaLogin) {
            loginAttemptRepository.addFingerprint(user.username, deviceFingerprint)
            val accessToken = tokenService.generateAccessToken(user.username)
            jpaLoginAttemptRepository.resetAttempt(user.username)
            return LoginAttempt.Success(accessToken)
        }
        return if (user.mfa) {
            LoginAttempt.MFALogin
        } else {
            val sessionId = UUID.randomUUID().toString()
            emailService.sendLoginEmail(
                user.email, sessionId, verificationEmailExpirationMinutes
            )
            cacheRepository.storeHash(
                Constants.USER_SESSION_PREFIX + sessionId, mapOf(
                    MAP_KEY_USERNAME to user.username,
                    MAP_KEY_DEVICE_FINGERPRINT to deviceFingerprint,
                ), ttl = verificationEmailExpirationMinutes.toLong(), unit = TimeUnit.MINUTES
            )
            LoginAttempt.CredentialVerification
        }
    }
}

sealed interface LoginAttempt {
    data class NewDeviceLogin(val token: String) : LoginAttempt
    data object MFALogin : LoginAttempt
    data object InvalidCredentials : LoginAttempt
    data object CredentialVerification : LoginAttempt
    data class Success(val token: String) : LoginAttempt
}