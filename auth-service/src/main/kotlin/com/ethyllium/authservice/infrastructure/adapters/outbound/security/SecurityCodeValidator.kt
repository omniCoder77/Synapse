package com.ethyllium.authservice.infrastructure.adapters.outbound.security

import com.ethyllium.authservice.domain.port.driven.CodeValidator
import com.warrenstrange.googleauth.GoogleAuthenticator
import org.springframework.stereotype.Component

@Component
class SecurityCodeValidator: CodeValidator {
    override fun validateCode(secret: String, code: String): Boolean {
        return GoogleAuthenticator().authorize(secret, code.toInt())
    }
}