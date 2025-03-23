package com.ethyllium.authservice.exception

class InvalidCredentialsException(override val message: String) : RuntimeException("Invalid credentials")