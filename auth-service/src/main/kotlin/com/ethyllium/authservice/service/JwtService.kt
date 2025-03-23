package com.ethyllium.authservice.service

import com.ethyllium.authservice.jwt.JwtKeyManager
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.token.access.token.expiry}") private val accessTokenExpiration: Long,
    @Value("\${jwt.token.refresh.token.expiry}") private val refreshTokenExpiration: Long,
    @Value("\${issuer}") private val issuer: String,
    private val jwtKeyManager: JwtKeyManager
) {

    val key = jwtKeyManager.getKey()
    fun generateAccessToken(subject: String, additionalClaims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpiration)

        return Jwts.builder().claims(additionalClaims).subject(subject).issuedAt(now).expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256).compact()
    }
    fun generateRefreshToken(subject: String, additionalClaims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpiration)

        return Jwts.builder().claims(additionalClaims).subject(subject).issuedAt(now).expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256).compact()
    }

    /**
     * Validates the provided JWT token.
     * @param token the JWT token to validate
     * @return true if the token is valid; false otherwise.
     */
    fun validateToken(token: String): String? {
        return try {
            val claims = getClaims(token)?.subject
            claims
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Extracts all claims from the provided JWT token.
     * @param token the JWT token
     * @return the claims contained in the token or null if parsing fails.
     */
    fun getClaims(token: String): Claims? {
        return try {
            Jwts.parser().verifyWith(jwtKeyManager.getKey()).build().parseSignedClaims(token).payload
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Retrieves the subject (e.g., user id or username) from the JWT token.
     * @param token the JWT token
     * @return the subject if available; null otherwise.
     */
    fun getSubject(token: String): String? {
        return getClaims(token)?.subject
    }
}
