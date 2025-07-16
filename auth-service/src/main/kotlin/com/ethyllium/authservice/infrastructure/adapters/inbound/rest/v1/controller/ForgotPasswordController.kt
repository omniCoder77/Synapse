package com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.controller

import com.ethyllium.authservice.application.service.ValidationService
import com.ethyllium.authservice.domain.port.driven.CacheRepository
import com.ethyllium.authservice.domain.port.driven.EmailService
import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.domain.port.driven.UserRepository
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.ApiResponse
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.NewPasswordRequest
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.PasswordResetRequest
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.PasswordResetResponse
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.PasswordResetTokenResponse
import com.ethyllium.authservice.infrastructure.adapters.inbound.rest.v1.dto.ValidateTokenRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.temporal.ChronoUnit
import java.util.*

@RestController
@RequestMapping("/api/v1/auth")
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
    fun initiatePasswordReset(@Valid @RequestBody request: PasswordResetRequest): Mono<ResponseEntity<ApiResponse>> {
        return userRepository.findByEmail(request.email).flatMap { user ->
            if (user == null) {
                return@flatMap Mono.just(
                    ResponseEntity.accepted()
                        .body(ApiResponse.Success("If an account exists with this email, a reset link has been sent"))
                )
            }

            val resetToken = tokenService.generateSecureToken()
            val tokenHash = passwordEncoder.encode(resetToken)

            cacheRepository.store(
                key = RESET_TOKEN_PREFIX + user.email, data = mapOf(
                    "tokenHash" to tokenHash, "userId" to user.username
                ), ttl = RESET_TOKEN_EXPIRY_MINUTES, unit = ChronoUnit.MINUTES
            ).subscribeOn(Schedulers.boundedElastic()).subscribe()

            emailService.sendPasswordResetEmail(
                email = user.email, resetToken = resetToken, expirationMinutes = RESET_TOKEN_EXPIRY_MINUTES.toInt()
            ).subscribeOn(Schedulers.boundedElastic()).subscribe()

            Mono.just(
                ResponseEntity.accepted()
                    .body(ApiResponse.Success("If an account exists with this email, a reset link has been sent"))
            )
        }
    }

    @PostMapping("/validate-reset-token")
    fun validateResetToken(@Valid @RequestBody request: ValidateTokenRequest): Mono<ResponseEntity<ApiResponse>> {
        val cacheKey = RESET_TOKEN_PREFIX + request.email

        return cacheRepository.readHash(cacheKey).flatMap { cachedData ->
            val tokenHash = cachedData["tokenHash"]
            val userId = cachedData["userId"]

            if (tokenHash == null || userId == null || !passwordEncoder.matches(request.token, tokenHash)) {
                return@flatMap Mono.just(
                    ResponseEntity.badRequest().body(ApiResponse.Error("Invalid token") as ApiResponse)
                )
            }

            userRepository.findUserByUsername(UUID.fromString(userId)).flatMap { user ->
                val sessionToken = tokenService.generateSecureToken()

                cacheRepository.store(
                    RESET_SESSION_PREFIX + sessionToken,
                    mapOf("userId" to userId, "email" to request.email),
                    15,
                    ChronoUnit.MINUTES
                ).thenReturn(
                    ResponseEntity.ok(
                        ApiResponse.Success(
                            PasswordResetResponse(
                                mfaRequired = user.totp != null, sessionToken = sessionToken
                            )
                        ) as ApiResponse
                    )
                )
            }
        }.switchIfEmpty(
            Mono.just(
                ResponseEntity.badRequest().body(ApiResponse.Error("Invalid or expired token") as ApiResponse)
            )
        )
    }

    @PostMapping("/verify-mfa-password")
    fun verifyMfa(
        @RequestParam code: String, @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String
    ): Mono<ResponseEntity<ApiResponse>> {
        val sessionToken = authHeader.removePrefix("Bearer ").trim()
        val sessionKey = RESET_SESSION_PREFIX + sessionToken

        return cacheRepository.readHash(sessionKey).flatMap { sessionData ->
            val userId = sessionData["userId"] ?: return@flatMap Mono.just(
                ResponseEntity.badRequest().body(ApiResponse.Error("Invalid session") as ApiResponse)
            )

            userRepository.findUserByUsername(UUID.fromString(userId)).flatMap { user ->
                if (user.totp == null) {
                    return@flatMap Mono.just(
                        ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                            .body(ApiResponse.Error("MFA not configured for this user") as ApiResponse)
                    )
                }

                val attemptsKey = MFA_ATTEMPTS_PREFIX + userId

                cacheRepository.read(attemptsKey).defaultIfEmpty(0).flatMap { rawAttempts ->
                    val attempts = (rawAttempts as? Int) ?: 0

                    if (attempts >= MFA_ATTEMPT_LIMIT) {
                        return@flatMap Mono.just(
                            ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                .body(ApiResponse.Error("Too many attempts. Please try again later.") as ApiResponse)
                        )
                    }

                    val isValid = validationService.validateCode(user.totp!!, code)
                    if (!isValid) {
                        return@flatMap cacheRepository.store(attemptsKey, attempts + 1, 15, ChronoUnit.MINUTES)
                            .thenReturn(
                                ResponseEntity.badRequest().body(ApiResponse.Error("Invalid MFA code") as ApiResponse)
                            )
                    }

                    // Clear attempts and generate token
                    cacheRepository.remove(attemptsKey).thenReturn(
                        ResponseEntity.ok(
                            ApiResponse.Success(
                                PasswordResetTokenResponse(
                                    tokenService.generateAccessToken(
                                        subject = userId, additionalClaims = mapOf(
                                            MFA_VERIFIED to true, "reset_session" to sessionToken
                                        )
                                    )
                                )
                            ) as ApiResponse
                        )
                    )
                }
            }
        }.switchIfEmpty(
            Mono.just(
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.Error("Invalid session") as ApiResponse)
            )
        )
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String, @Valid @RequestBody request: NewPasswordRequest
    ): Mono<ResponseEntity<ApiResponse>> {
        val resetToken = authHeader.removePrefix("Bearer ").trim()
        val claims = tokenService.getClaims(resetToken)

        // Immediate blocking validation
        if (claims == null) {
            return Mono.just(
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.Error("Invalid or expired token") as ApiResponse)
            )
        }

        if (claims[MFA_VERIFIED] != true) {
            return Mono.just(
                ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.Error("MFA verification required") as ApiResponse)
            )
        }

        val userId = claims.subject ?: return Mono.just(
            ResponseEntity.badRequest().body(ApiResponse.Error("Invalid token") as ApiResponse)
        )

        val sessionToken = claims["reset_session"] as? String ?: return Mono.just(
            ResponseEntity.badRequest().body(ApiResponse.Error("Invalid session reference") as ApiResponse)
        )

        if (request.newPassword != request.confirmPassword) {
            return Mono.just(
                ResponseEntity.badRequest().body(ApiResponse.Error("Passwords do not match") as ApiResponse)
            )
        }

        return userRepository.findUserByUsername(UUID.fromString(userId)).flatMap { user ->
            val updatedUser = user.copy(_password = passwordEncoder.encode(request.newPassword))

            userRepository.addUser(updatedUser.toUser(), updatedUser.refreshToken, updatedUser.totp).flatMap {
                Mono.zip(
                    cacheRepository.remove(RESET_SESSION_PREFIX + sessionToken),
                    cacheRepository.remove(RESET_TOKEN_PREFIX + user.email)
                ).thenReturn(
                    ResponseEntity.ok(
                        ApiResponse.Success("Password successfully reset") as ApiResponse
                    )
                )
            }
        }.switchIfEmpty(
            Mono.just(ResponseEntity.badRequest().body(ApiResponse.Error("User not found") as ApiResponse))
        )
    }
}