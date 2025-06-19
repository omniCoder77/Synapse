package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.application.dto.request.RegisterRequest
import com.ethyllium.authservice.domain.port.driven.*
import com.ethyllium.authservice.domain.port.driver.QrCodeGenerator
import com.ethyllium.authservice.domain.util.Constants.Companion.EMAIL_TOKEN_PREFIX
import com.ethyllium.authservice.domain.util.CredentialValidator
import com.ethyllium.authservice.infrastructure.persistence.jpa.LoginAttemptEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional
class RegisterUseCase(
    private val userRepository: UserRepository,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val tokenService: TokenService,
    private val totpSecretGenerator: TotpSecretGenerator,
    private val qrCodeGenerator: QrCodeGenerator,
    private val emailService: EmailService,
    private val cacheRepository: CacheRepository,
    @Value("\${mail.token.verification-ms}") private val emailVerificationTimeMilli: Int,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(RegisterUseCase::class.java)

    fun register(registerRequest: RegisterRequest): RegisterResult {
        val email = registerRequest.email
        registerRequest.apply {
            password = passwordEncoder.encode(password)
        }
        try {
            if (userRepository.existsUserByEmail(email)) {
                return RegisterResult.Failure("Username already exists")
            }
            val credentialValidity = CredentialValidator().validateCredential(email, registerRequest.password)
            if (credentialValidity != null) {
                return RegisterResult.Failure(credentialValidity)
            }
            val user = registerRequest.toUser()
            val refreshToken = tokenService.generateRefreshToken(user.username)
            val mfaTotp = if (user.isMfaEnabled) {
                totpSecretGenerator.generateTotpSecret(user.email)
            } else {
                null
            }
            val addedUser = userRepository.addUser(
                user, refreshToken = refreshToken, mfaTotp = mfaTotp?.second
            )
            val loginAttempt = LoginAttemptEntity(
                username = user.username, deviceFingerprint = mutableListOf(registerRequest.deviceFingerprint)
            )
            loginAttemptRepository.save(loginAttempt)
            val verificationToken = addedUser.username
//            otpDeliveryService.sendOtp(phoneNumber = addedUser.phoneNumber).subscribe()
            emailService.sendVerificationEmail(
                addedUser.email, token = verificationToken, expirationMinutes = emailVerificationTimeMilli / 1000 / 60
            )

            cacheRepository.store(
                key = "$EMAIL_TOKEN_PREFIX$verificationToken",
                ttl = emailVerificationTimeMilli.toLong(),
                unit = TimeUnit.MILLISECONDS,
                data = addedUser.username
            )
            return if (mfaTotp != null) {
                val qrCode = qrCodeGenerator.generateQrCode(uri = mfaTotp.first)
                RegisterResult.MfaImage(qrCode)
            } else {
                val accessToken = tokenService.generateAccessToken(user.username)
                RegisterResult.Token(accessToken, addedUser.refreshToken)
            }
        } catch (e: Exception) {
            logger.error("Registration failed for user ${registerRequest.email}", e)
            return RegisterResult.Failure("Registration failed: ${e.message}")
        }
    }
}

sealed class RegisterResult {
    data class Token(val accessToken: String, val refreshToken: String) : RegisterResult()
    data class MfaImage(val mfaQrCode: ByteArray) : RegisterResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as MfaImage

            return mfaQrCode.contentEquals(other.mfaQrCode)
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + mfaQrCode.contentHashCode()
            return result
        }
    }

    data class Failure(val error: String) : RegisterResult()

    override fun equals(other: Any?): Boolean {
        if (this is MfaImage && other is MfaImage) {
            return this.mfaQrCode.contentEquals(other.mfaQrCode)
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        if (this is MfaImage) {
            return mfaQrCode.contentHashCode()
        }
        return super.hashCode()
    }
}