package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.application.dto.response.ApiResponse
import com.ethyllium.authservice.application.service.ValidationService
import com.ethyllium.authservice.domain.port.driven.CacheRepository
import com.ethyllium.authservice.domain.port.driven.EmailService
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/auth")
class PasswordResetController(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val emailService: EmailService,
    private val passwordEncoder: PasswordEncoder,
    private val cacheRepository: CacheRepository,
    private val validationService: ValidationService
) {
    companion object {
        private const val RESET_TOKEN_EXPIRY_MINUTES = 15L
        private const val MFA_ATTEMPT_LIMIT = 5
        private const val RESET_TOKEN_PREFIX = "reset_token:"
        private const val MFA_ATTEMPTS_PREFIX = "mfa_attempts:"
        private const val RESET_SESSION_PREFIX = "reset_session:"
        private const val MFA_VERIFIED = "mfa_verified"
    }

    @PostMapping("/forgot-password")
    fun initiatePasswordReset(@Valid @RequestBody request: PasswordResetRequest): ResponseEntity<ApiResponse> {
        val user = userRepository.findByEmail(request.email) ?: run {
            return ResponseEntity.accepted()
                .body(ApiResponse.Success("If an account exists with this email, a reset link has been sent"))
        }

        val resetToken = tokenService.generateSecureToken()
        val tokenHash = passwordEncoder.encode(resetToken)

        cacheRepository.store(
            key = RESET_TOKEN_PREFIX + user.email, data = mapOf(
                "tokenHash" to tokenHash, "userId" to user.username
            ), ttl = RESET_TOKEN_EXPIRY_MINUTES, unit = TimeUnit.MINUTES
        )

        emailService.sendPasswordResetEmail(
            email = user.email, resetToken = resetToken, expirationMinutes = RESET_TOKEN_EXPIRY_MINUTES.toInt()
        )

        return ResponseEntity.accepted()
            .body(ApiResponse.Success("If an account exists with this email, a reset link has been sent"))
    }

    @PostMapping("/validate-reset-token")
    fun validateResetToken(@Valid @RequestBody request: ValidateTokenRequest): ResponseEntity<ApiResponse> {
        val cachedData =
            cacheRepository.readHash(RESET_TOKEN_PREFIX + request.email) ?: return ResponseEntity.badRequest()
                .body(ApiResponse.Error("Invalid or expired token"))

        val tokenHash = cachedData["tokenHash"] ?:return ResponseEntity.badRequest().body(ApiResponse.Error("Invalid token"))
        val userId = cachedData["userId"] ?: return ResponseEntity.badRequest().body(ApiResponse.Error("Invalid token"))

        if (!passwordEncoder.matches(request.token, tokenHash)) {
            return ResponseEntity.badRequest().body(ApiResponse.Error("Invalid token"))
        }

        val user = userRepository.findUserByUsername(userId) ?: throw EntityNotFoundException("User not found")

        val sessionToken = tokenService.generateSecureToken()
        cacheRepository.store(
            key = RESET_SESSION_PREFIX + sessionToken, data = mapOf(
                "userId" to userId, "email" to request.email
            ), ttl = 15, unit = TimeUnit.MINUTES
        )
        val response = PasswordResetResponse(
            mfaRequired = user.totp != null, sessionToken = sessionToken
        )

        return ResponseEntity.ok(ApiResponse.Success(response))
    }

    @PostMapping("/verify-mfa-password")
    fun verifyMfa(
        @RequestParam code: String, @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String
    ): ResponseEntity<ApiResponse> {
        val sessionToken = authHeader.removePrefix("Bearer ").trim()
        val sessionData = cacheRepository.readHash(RESET_SESSION_PREFIX + sessionToken) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED
        ).body(ApiResponse.Error("Invalid session"))

        val userId =
            sessionData["userId"] ?: return ResponseEntity.badRequest().body(ApiResponse.Error("Invalid session"))

        val user = userRepository.findUserByUsername(userId) ?: throw EntityNotFoundException("User not found")

        if (user.totp == null) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(ApiResponse.Error("MFA not configured for this user"))
        }

        val attemptsKey = MFA_ATTEMPTS_PREFIX + userId
        val attempts = (cacheRepository.read(attemptsKey) as? Int) ?: 0
        if (attempts >= MFA_ATTEMPT_LIMIT) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.Error("Too many attempts. Please try again later."))
        }

        val isValid = validationService.validateCode(user.totp!!, code)
        if (!isValid) {
            cacheRepository.store(attemptsKey, attempts + 1, 15, TimeUnit.MINUTES)
            return ResponseEntity.badRequest().body(ApiResponse.Error("Invalid MFA code"))
        }

        // Clear attempts on success
        cacheRepository.remove(attemptsKey)

        // Generate final reset token with MFA verification claim
        val resetToken = tokenService.generateAccessToken(
            subject = userId, additionalClaims = mapOf(
                MFA_VERIFIED to true, "reset_session" to sessionToken
            )
        )

        return ResponseEntity.ok(ApiResponse.Success(PasswordResetTokenResponse(resetToken)))
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String, @Valid @RequestBody request: NewPasswordRequest
    ): ResponseEntity<ApiResponse> {
        val resetToken = authHeader.removePrefix("Bearer ").trim()

        // Verify token and MFA claim
        val claims = tokenService.getClaims(resetToken) ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.Error("Invalid or expired token"))

        if (claims[MFA_VERIFIED] != true) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.Error("MFA verification required"))
        }

        val userId = claims.subject ?: return ResponseEntity.badRequest().body(ApiResponse.Error("Invalid token"))

        val sessionToken = claims["reset_session"] as? String ?: return ResponseEntity.badRequest()
            .body(ApiResponse.Error("Invalid session reference"))

        // Verify passwords match
        if (request.newPassword != request.confirmPassword) {
            return ResponseEntity.badRequest().body(ApiResponse.Error("Passwords do not match"))
        }

        val user = userRepository.findUserByUsername(userId) ?: return ResponseEntity.badRequest()
            .body(ApiResponse.Error("User not found"))

        // Update password
        user._password = passwordEncoder.encode(request.newPassword)
        userRepository.addUser(user.toUser(), user.refreshToken, user.totp)

        // Clean up all related cache entries
        cacheRepository.remove(RESET_SESSION_PREFIX + sessionToken)
        cacheRepository.remove(RESET_TOKEN_PREFIX + user.email)

        return ResponseEntity.ok(ApiResponse.Success("Password successfully reset"))
    }
}

data class PasswordResetRequest(val email: String)
data class ValidateTokenRequest(val email: String, val token: String)
data class NewPasswordRequest(val newPassword: String, val confirmPassword: String)
data class PasswordResetResponse(
    val mfaRequired: Boolean,
    val sessionToken: String? = null,
    val resetToken: String? = null,
    val mfaTypes: List<String>? = null
)

data class PasswordResetTokenResponse(val resetToken: String)
