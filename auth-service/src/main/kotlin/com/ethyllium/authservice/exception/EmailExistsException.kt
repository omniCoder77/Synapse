package com.ethyllium.authservice.exception

class EmailExistsException(val email: String) : RuntimeException("Email $email already exists")