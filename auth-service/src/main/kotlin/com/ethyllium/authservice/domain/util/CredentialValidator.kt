package com.ethyllium.authservice.domain.util

import org.apache.commons.validator.routines.EmailValidator

class CredentialValidator {
    fun validateCredential(email: String, password: String): String? {
        if (email.isBlank()) {
            return "Email cannot be empty"
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            return "Invalid email format"
        }
        if (password.isBlank()) {
                return "Password cannot be empty"
            }

            val minLength = 8
            if (password.length < minLength) {
                return "Password must be at least $minLength characters long"
            }

            if (!password.any { it.isLowerCase() }) {
                return "Password must contain at least one lowercase letter"
            }

            if (!password.any { it.isUpperCase() }) {
                return "Password must contain at least one uppercase letter"
            }

            if (!password.any { it.isDigit() }) {
                return "Password must contain at least one digit"
            }

            return null
    }
}