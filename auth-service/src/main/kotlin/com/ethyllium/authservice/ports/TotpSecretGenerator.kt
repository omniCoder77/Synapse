package com.ethyllium.authservice.ports

interface TotpSecretGenerator {
    fun generateTotpSecret(email: String): Pair<String, String>
}