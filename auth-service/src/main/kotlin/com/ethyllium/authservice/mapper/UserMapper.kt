package com.ethyllium.authservice.mapper

import com.ethyllium.authservice.dto.request.RegisterRequest
import com.ethyllium.authservice.model.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val passwordEncoder: PasswordEncoder
) {
    fun toUser(registerRequest: RegisterRequest) = User(
        userName = registerRequest.username,
        _password = passwordEncoder.encode(registerRequest.password),
        email = registerRequest.email,
        mfa = registerRequest.f2a
    )
}