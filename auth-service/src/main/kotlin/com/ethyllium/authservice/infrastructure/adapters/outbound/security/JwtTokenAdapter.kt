package com.ethyllium.authservice.infrastructure.adapters.outbound.security

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.adapters.outbound.jwt.JwtKeyManager
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtTokenAdapter(
    @Value("\${jwt.token.access.token.expiry}") private val accessTokenExpiration: Long,
    @Value("\${jwt.token.refresh.token.expiry}") private val refreshTokenExpiration: Long,
    private val jwtKeyManager: JwtKeyManager
) : TokenService {

    private lateinit var key: SecretKey
    private lateinit var jwtParser: JwtParser

    @PostConstruct
    fun init() {
        runBlocking {
            key = jwtKeyManager.getKey()
            // Initialize parser AFTER key is loaded
            jwtParser = Jwts.parser().verifyWith(key).build()
        }
    }

    override fun generateAccessToken(subject: String, additionalClaims: Map<String, Any>): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpiration)

        return Jwts.builder().claims(additionalClaims).subject(subject).issuedAt(now).expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256).compact()
    }

    override fun generateRefreshToken(subject: String, additionalClaims: Map<String, Any>): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpiration)

        return Jwts.builder().claims(additionalClaims).subject(subject).issuedAt(now).expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256).compact()
    }

    /**
     * Validates the provided JWT token.
     * @param token the JWT token to validate
     * @return the subject of the token, else null if the token is invalid
     */
    override fun validateToken(token: String): String? {
        return try {
            jwtParser.parseSignedClaims(token).payload.subject // Single parsing
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Extracts all claims from the provided JWT token.
     * @param token the JWT token
     * @return the claims contained in the token or null if parsing fails.
     */
    override fun getClaims(token: String): Claims? {
        return try {
            jwtParser.parseSignedClaims(token).payload
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Retrieves the subject (e.g., user id or username) from the JWT token.
     * @param token the JWT token
     * @return the subject if available; null otherwise.
     */
    override fun getSubject(token: String): String? {
        return getClaims(token)?.subject
    }

    override fun generateSecureToken(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
