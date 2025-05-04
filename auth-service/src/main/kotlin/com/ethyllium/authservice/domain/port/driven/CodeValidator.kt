package com.ethyllium.authservice.domain.port.driven

interface CodeValidator {
    fun validateCode(secret: String, code: String): Boolean
}