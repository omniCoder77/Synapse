package com.ethyllium.authservice.domain.port.driven

interface TotpSecretGenerator {
    fun generateTotpSecret(email: String): Pair<String, String>
}