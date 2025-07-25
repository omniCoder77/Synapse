package com.ethyllium.authservice.domain.port.driven

import io.jsonwebtoken.Claims

interface TokenService {
    fun generateAccessToken(subject: String, additionalClaims: Map<String, Any> = emptyMap()): String
    fun generateRefreshToken(subject: String, additionalClaims: Map<String, Any> = emptyMap()): String
    fun validateToken(token: String): String?
    fun getClaims(token: String): Claims?
    fun getSubject(token: String): String?
    fun generateSecureToken(): String
}