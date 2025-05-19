package com.ethyllium.authservice.application.service

import com.ethyllium.authservice.application.dto.request.RegisterRequest
import com.ethyllium.authservice.domain.port.driven.*
import com.ethyllium.authservice.domain.port.driver.QrCodeGenerator
import com.ethyllium.authservice.domain.service.LockService
import com.ethyllium.authservice.domain.util.CredentialValidator
import com.ethyllium.authservice.infrastructure.persistence.jpa.LoginAttemptEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Service
@Transactional
class RegisterUseCase(
    private val userRepository: UserRepository,
    private val loginAttemptRepository: LoginAttemptRepository,
    private val tokenService: TokenService,
    private val totpSecretGenerator: TotpSecretGenerator,
    private val qrCodeGenerator: QrCodeGenerator,
    private val otpDeliveryService: OtpDeliveryService,
    private val otpGenerator: OtpGenerator,
    private val emailService: EmailService,
    @Qualifier("registrationTaskExecutor") private val taskExecutor: TaskExecutor,
    private val cacheRepository: CacheRepository,
    private val lockService: LockService
) {
    companion object {
        private const val TOKEN_PREFIX = "verification_token:"
        private const val LOCK_PREFIX = "register_lock:"
        private val userRegistrationLock = ConcurrentHashMap<String, ReentrantLock>()
        private val logger = LoggerFactory.getLogger(RegisterUseCase::class.java)
    }

fun register(registerRequest: RegisterRequest): RegisterResult {
    val username = registerRequest.username
    val redisLockKey = "$LOCK_PREFIX$username"
    val lockValue = UUID.randomUUID().toString()

    val redisLockAcquired = lockService.lockAcquired(redisLockKey, lockValue, ttl = 1000).toCompletableFuture().get()
    if (redisLockAcquired != true) {
        return RegisterResult.Failure("Registration in progress, please try again")
    }

    val lock = userRegistrationLock.computeIfAbsent(username) { ReentrantLock() }

    if (!lock.tryLock(500, TimeUnit.MILLISECONDS)) {
        cacheRepository.remove(redisLockKey)
        return RegisterResult.Failure("Registration in progress, please try again")
    }

    try {
        if (userRepository.existsByUsername(username)) {
            return RegisterResult.Failure("Username already exists")
        }

        val credentialValidity = CredentialValidator().validateCredential(username, registerRequest.password)

        if (credentialValidity != null) {
            return RegisterResult.Failure(credentialValidity)
        }

        val user = registerRequest.toUser()

        val refreshTokenFuture = CompletableFuture.supplyAsync {
            tokenService.generateRefreshToken(user.username)
        }

        val mfaTotpFuture = if (user.isMfaEnabled) {
            CompletableFuture.supplyAsync {
                totpSecretGenerator.generateTotpSecret(user.email)
            }
        } else {
            CompletableFuture.completedFuture(null)
        }

        val refreshToken = refreshTokenFuture.get()
        val mfaTotp = mfaTotpFuture.get()

        val addedUser = userRepository.addUser(
            user, refreshToken = refreshToken, mfaTotp = mfaTotp?.second
        )

        val loginAttemptFuture = CompletableFuture.runAsync({
            val loginAttempt = LoginAttemptEntity(
                username = username,
                deviceFingerprint = mutableListOf(registerRequest.deviceFingerprint)
            )
            loginAttemptRepository.save(loginAttempt)
        }, taskExecutor)

        val otpFuture = CompletableFuture.supplyAsync({
            otpGenerator.generateOtp(userId = addedUser.username)
        }, taskExecutor)

        val verificationTokenFuture = CompletableFuture.supplyAsync({
            tokenService.generateRefreshToken(addedUser.username)
        }, taskExecutor)

        val otp = otpFuture.get()
        val verificationToken = verificationTokenFuture.get()

        CompletableFuture.allOf(
            CompletableFuture.runAsync({
                otpDeliveryService.sendOtp(
                    otp = otp,
                    userId = addedUser.username,
                    phoneNumber = addedUser.phoneNumber
                )
            }, taskExecutor),

            CompletableFuture.runAsync({
                emailService.sendVerificationEmail(
                    addedUser.email, token = verificationToken
                )
            }, taskExecutor),

            CompletableFuture.runAsync({
                cacheRepository.store(
                    key = "$TOKEN_PREFIX$verificationToken",
                    ttl = 5,
                    unit = TimeUnit.MINUTES,
                    data = addedUser.username
                )
            }, taskExecutor),

            loginAttemptFuture
        ).join()

        return if (mfaTotp != null) {
            val qrCode = qrCodeGenerator.generateQrCode(uri = mfaTotp.first)
            RegisterResult.MfaImage(qrCode)
        } else {
            val accessToken = tokenService.generateAccessToken(user.username)
            RegisterResult.Token(accessToken, addedUser.refreshToken)
        }
    } catch (e: Exception) {
        logger.error("Registration failed for user ${registerRequest.username}", e)
        return RegisterResult.Failure("Registration failed: ${e.message}")
    } finally {
        lock.unlock()
        userRegistrationLock.remove(username, lock)
        cacheRepository.remove(redisLockKey)
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

            if (!mfaQrCode.contentEquals(other.mfaQrCode)) return false

            return true
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