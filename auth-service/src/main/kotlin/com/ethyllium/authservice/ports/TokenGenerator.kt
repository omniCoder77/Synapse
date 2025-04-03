package com.ethyllium.authservice.ports

interface TokenGenerator {
    fun generateToken(userId: String): String
}