package com.ethyllium.authservice.validation

import org.apache.commons.validator.routines.EmailValidator

class ValidateUserCredentials {
    companion object {
        fun validateEmail(email: String): String? {
            if (email.isBlank()) {
                return "Email cannot be empty"
            }

            return if (!EmailValidator.getInstance().isValid(email)) {
                "Invalid email format"
            } else {
                null // Email is valid
            }
        }

        fun validatePassword(password: String): String? {
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

            return null // Password is valid
        }
    }
}