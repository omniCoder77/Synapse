package com.ethyllium.authservice.domain.exception

class InvalidCredentialsException(override val message: String) : RuntimeException("Invalid credentials")