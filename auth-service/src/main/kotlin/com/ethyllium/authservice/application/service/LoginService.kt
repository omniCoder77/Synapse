package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.domain.port.driven.EmailService
import com.ethyllium.authservice.domain.port.driven.OtpGenerator
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaUserEntityRepository
import com.ethyllium.authservice.infrastructure.persistence.jpa.JpaLoginAttemptRepository
import com.ethyllium.authservice.infrastructure.persistence.jpa.UserEntity
import com.ethyllium.authservice.util.MfaPurpose
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@Service
@EnableAsync
@Transactional
class LoginService(
    private val userRepository: JpaUserEntityRepository,
    private val jpaLoginAttemptRepository: JpaLoginAttemptRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val otpGenerator: OtpGenerator,
    private val emailService: EmailService,
    private val redisTemplate: StringRedisTemplate
) {

    @Async("asyncTaskExecutor")
    fun sendEmailAsync(email: String, token: String) {
        emailService.sendVerificationEmail(email, token)
    }

    fun login(email: String, password: String, deviceFingerprint: String): LoginAttempt {
        val (user, _) = userRepository.findUserAndAttemptByEmail(email) ?: throw UsernameNotFoundException(email)

        if (!isPasswordValid(password, user.password)) {
            return LoginAttempt.InvalidCredentials
        }

        val cachedFingerprints = redisTemplate.opsForSet().members("fingerprints:${user.username}")
        val isKnownDevice = cachedFingerprints?.contains(deviceFingerprint) ?: false

        if (!isKnownDevice) {
            return handleNewDevice(user, deviceFingerprint)
        }

        return if (user.mfa) {
            val mfaToken = generateMfaToken(user.username, MfaPurpose.LOGIN)
            LoginAttempt.MFALogin(mfaToken)
        } else {
            val accessToken = tokenService.generateAccessToken(user.username)
            jpaLoginAttemptRepository.resetAttempt(user.username)
            LoginAttempt.Success(accessToken)
        }
    }

    private fun isPasswordValid(rawPassword: String, encodedPassword: String): Boolean {
        val preHashed = sha256(rawPassword)
        return passwordEncoder.matches(preHashed, encodedPassword)
    }


    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return BigInteger(1, hashBytes).toString(16).padStart(64, '0')
    }

    private fun handleNewDevice(user: UserEntity, deviceFingerprint: String): LoginAttempt {
        redisTemplate.opsForSet().add("fingerprints:${user.username}", deviceFingerprint)

        return if (user.mfa) {
            val mfaToken = generateMfaToken(user.username, MfaPurpose.NEW_DEVICE_LOGIN)
            LoginAttempt.MFALogin(mfaToken)
        } else {
            val token = otpGenerator.generateOtp(user.username)
            sendEmailAsync(user.email, token)
            LoginAttempt.CredentialVerification
        }
    }

    private fun generateMfaToken(username: String, purpose: MfaPurpose): String {
        val cachedToken = redisTemplate.opsForValue().get("mfa:$username:$purpose") as String?
        return cachedToken ?: tokenService.generateAccessToken(
            username, additionalClaims = mapOf(purpose.name to "mfa_purpose")
        ).also { redisTemplate.opsForValue().set("mfa:$username:$purpose", it, 5, TimeUnit.MINUTES) }
    }
}

sealed interface LoginAttempt {
    data class NewDeviceLogin(val token: String) : LoginAttempt
    data class MFALogin(val token: String) : LoginAttempt
    data object InvalidCredentials : LoginAttempt
    data object CredentialVerification : LoginAttempt
    data class Success(val token: String) : LoginAttempt
}