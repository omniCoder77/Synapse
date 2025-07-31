package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.application.util.LoginConstants
import com.ethyllium.authservice.domain.model.LoginAttempt
import com.ethyllium.authservice.domain.port.driven.*
import com.ethyllium.authservice.domain.port.driver.LoginUseCase
import com.ethyllium.authservice.domain.util.Constants
import com.ethyllium.authservice.infrastructure.adapters.outbound.persistence.postgresql.entity.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@EnableAsync
@Transactional
class LoginUseCaseImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val emailService: EmailService,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val cacheRepository: CacheRepository,
    @Value("\${mail.login.verification-min}") private val verificationEmailExpirationMinutes: Int,
) : LoginUseCase {

    /**
     * The login use case.
     * @param email - The email of the user trying to log in.
     * @param password - The password of the user.
     * @param isMfaLogin - Whether this login attempt is MFA verified or not
     */

    override fun login(
        email: String,
        password: String,
        deviceFingerprint: String,
        isMfaLogin: Boolean
    ): Mono<LoginAttempt> {

        return userRepository.findByEmail(email).flatMap { user ->
            if (!isPasswordValid(password, user._password)) {
                Mono.just(LoginAttempt.InvalidCredentials)
            } else {
                loginAttemptRepository.getFingerprints(user.username).flatMap { fingerprints ->
                    val isKnownDevice = fingerprints.contains(deviceFingerprint)
                    if (!isKnownDevice) handleNewDevice(user, deviceFingerprint, isMfaLogin)
                    else {
                        if (user.mfa && !isMfaLogin) {
                            Mono.just(LoginAttempt.MFALogin)
                        } else {
                            val accessToken = tokenService.generateAccessToken(user.username.toString())
                            loginAttemptRepository.resetAttempt(user.username).map { LoginAttempt.Success(accessToken) }
                        }
                    }
                }
            }
        }
    }

    private fun isPasswordValid(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    private fun handleNewDevice(user: UserEntity, deviceFingerprint: String, isMfaLogin: Boolean): Mono<LoginAttempt> {
        return if (isMfaLogin) {
            loginAttemptRepository.addFingerprint(user.username, deviceFingerprint).flatMap {
                val accessToken = tokenService.generateAccessToken(user.username.toString())
                loginAttemptRepository.resetAttempt(user.username).then(Mono.just(LoginAttempt.Success(accessToken)))
            }
        } else {
            val sessionId = UUID.randomUUID().toString()
            val emailMono = emailService.sendLoginEmail(
                user.email, sessionId, verificationEmailExpirationMinutes
            )
            val cacheMono = cacheRepository.storeHash(
                Constants.USER_SESSION_PREFIX + sessionId, mapOf(
                    LoginConstants.MAP_KEY_USERNAME to user.username,
                    LoginConstants.MAP_KEY_DEVICE_FINGERPRINT to deviceFingerprint,
                ), ttl = verificationEmailExpirationMinutes.toLong(), unit = ChronoUnit.MINUTES
            )

            Mono.zip(emailMono, cacheMono).then(Mono.just(LoginAttempt.MFALogin))
        }
    }
}

