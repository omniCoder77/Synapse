package com.ethyllium.authservice.domain.exception

class EmailExistsException(val email: String) : RuntimeException("Email $email already exists")