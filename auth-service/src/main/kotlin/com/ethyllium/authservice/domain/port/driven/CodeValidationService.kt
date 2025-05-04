package com.ethyllium.authservice.domain.port.driven

interface CodeValidationService {
    fun validateTotpCode(secret: String, code: String): Boolean
}