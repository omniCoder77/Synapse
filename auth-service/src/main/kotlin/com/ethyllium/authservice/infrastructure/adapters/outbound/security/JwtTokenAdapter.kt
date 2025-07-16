package com.ethyllium.authservice.infrastructure.adapters.outbound.security

import com.ethyllium.authservice.domain.port.driven.TokenService
import com.ethyllium.authservice.infrastructure.adapters.outbound.jwt.JwtKeyManager
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.*

@Service
class JwtTokenAdapter(
    @Value("\${jwt.token.access.token.expiry}") private val accessTokenExpiration: Long,
    @Value("\${jwt.token.refresh.token.expiry}") private val refreshTokenExpiration: Long,
    @Value("\${issuer}") private val issuer: String,
    private val jwtKeyManager: JwtKeyManager
) : TokenService {

    val key = jwtKeyManager.getKey()
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
            val claims = getClaims(token)?.subject
            claims
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
            Jwts.parser().verifyWith(jwtKeyManager.getKey()).build().parseSignedClaims(token).payload
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
        val token = ""
        SecureRandom().apply { nextBytes(token.toByteArray()) }
        return token
    }
}
